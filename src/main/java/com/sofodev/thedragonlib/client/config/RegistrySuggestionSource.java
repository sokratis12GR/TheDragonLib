package com.sofodev.thedragonlib.client.config;

import com.sofodev.thedragonlib.client.config.RegistryCandidates.Source;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link RuleSuggestionSource} backed by {@link RegistryCandidates}. Mods with {@code CUSTOM} fields extend it and
 * override the {@code custom*} methods. Wildcard options read {@code any.<source>} and {@code any.help} under the given
 * key prefix.
 *
 * @author Sokratis Fotkatzikis
 */
public class RegistrySuggestionSource implements RuleSuggestionSource {
    private final Map<Source, List<Option>> cache = new EnumMap<>(Source.class);
    private final String uiKeyPrefix;

    /** @param uiKeyPrefix the same prefix as {@link RuleListContext#uiKeyPrefix()} */
    public RegistrySuggestionSource(String uiKeyPrefix) {
        this.uiKeyPrefix = uiKeyPrefix;
    }

    private static @Nullable Source source(RuleType.Kind kind) {
        return switch (kind) {
            case ENTITY -> Source.ENTITY;
            case SPAWNABLE_ENTITY -> Source.SPAWNABLE_ENTITY;
            case ITEM -> Source.ITEM;
            case BLOCK -> Source.BLOCK;
            case BIOME -> Source.BIOME;
            case ATTRIBUTE -> Source.ATTRIBUTE;
            default -> null;
        };
    }

    private List<Option> registryOptions(Source source) {
        // Biomes depend on the loaded world, so only the others can be kept.
        if (source == Source.BIOME) return convert(source);
        return cache.computeIfAbsent(source, RegistrySuggestionSource::convert);
    }

    private static List<Option> convert(Source source) {
        return RegistryCandidates.of(source).stream().map(c -> new Option(c.id(), c.name().getString(), c.icon())).toList();
    }

    /** The options of a {@code CUSTOM} field. */
    protected List<Option> customOptions(RuleType.Field field, String[] values) {
        return List.of();
    }

    /** Whether a {@code CUSTOM} field accepts the id; by default only its listed options. */
    protected boolean customKnown(RuleType.Field field, String id, String[] values) {
        String normalized = normalize(id);
        return customOptions(field, values).stream().anyMatch(o -> o.id().equals(normalized));
    }

    /** The name of an id of a {@code CUSTOM} field; by default the name of its listed option. */
    protected String customName(RuleType.Field field, String id) {
        String normalized = normalize(id);
        return customOptions(field, new String[0]).stream().filter(o -> o.id().equals(normalized)).findFirst().map(Option::name).orElse(id);
    }

    /** Explanation shown when hovering an option of a {@code CUSTOM} field. */
    protected @Nullable Component customHelp(RuleType.Field field, String id) {
        return null;
    }

    /** Ids without a namespace mean {@code minecraft:}, like everywhere else in the game. */
    protected static String normalize(String id) {
        Identifier parsed = Identifier.tryParse(id);
        return parsed != null ? parsed.toString() : id;
    }

    @Override
    public List<Option> options(RuleType.Field field, String[] values) {
        if (field.kind() == RuleType.Kind.CUSTOM) return customOptions(field, values);
        Source source = source(field.kind());
        if (source == null) return List.of();
        List<Option> options = registryOptions(source);
        if (source != Source.ATTRIBUTE) return options;
        // A line's first field is what the attribute is set on; only offer what that entity has.
        AttributeSupplier supplier = values.length > 0 ? supplier(values[0]) : null;
        if (supplier == null) return options;
        return options.stream().filter(o -> supplier.hasAttribute(attribute(o.id()))).toList();
    }

    @Override
    public boolean isKnown(RuleType.Field field, String id, String[] values) {
        if (field.kind() == RuleType.Kind.CUSTOM) return customKnown(field, id, values);
        if (field.kind() == RuleType.Kind.BIOME && id.startsWith("#")) return Identifier.tryParse(id.substring(1)) != null;
        Source source = source(field.kind());
        return source != null && RegistryCandidates.isKnown(source, id);
    }

    @Override
    public String displayName(RuleType.Field field, String id) {
        if (id.equals("*")) return anyLabel(field).getString();
        if (field.kind() == RuleType.Kind.CUSTOM) return customName(field, id);
        Source source = source(field.kind());
        if (source == null) return id;
        String normalized = normalize(id);
        return registryOptions(source).stream().filter(o -> o.id().equals(normalized)).findFirst().map(Option::name).orElse(id);
    }

    @Override
    public Component anyLabel(RuleType.Field field) {
        return Component.translatable(uiKeyPrefix + "any." + field.name());
    }

    @Override
    public @Nullable Component help(RuleType.Field field, String id) {
        if (id.equals("*")) return Component.translatable(uiKeyPrefix + "any.help");
        return field.kind() == RuleType.Kind.CUSTOM ? customHelp(field, id) : null;
    }

    @Override
    public double @Nullable [] attributeRange(String attributeId) {
        if (!isAttribute(attributeId)) return null;
        return attribute(attributeId).value() instanceof RangedAttribute ranged
                ? new double[] {ranged.getMinValue(), ranged.getMaxValue()}
                : null;
    }

    @Override
    public @Nullable Double attributeDefault(@Nullable String context, String attributeId) {
        if (!isAttribute(attributeId)) return null;
        Holder<Attribute> attribute = attribute(attributeId);
        AttributeSupplier supplier = supplier(context);
        if (supplier != null && supplier.hasAttribute(attribute)) return supplier.getBaseValue(attribute);
        return attribute.value().getDefaultValue();
    }

    /** Default attributes of a mob, or null when the id isn't a mob. */
    @SuppressWarnings("unchecked")
    private static @Nullable AttributeSupplier supplier(@Nullable String entityId) {
        Identifier id = entityId == null ? null : Identifier.tryParse(entityId);
        if (id == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(id)) return null;
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(id);
        return DefaultAttributes.hasSupplier(type) ? DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) Objects.requireNonNull(type)) : null;
    }

    private static Holder<Attribute> attribute(String id) {
        return BuiltInRegistries.ATTRIBUTE.get(Identifier.parse(id)).orElseThrow();
    }

    private static boolean isAttribute(String id) {
        Identifier parsed = Identifier.tryParse(id);
        return parsed != null && BuiltInRegistries.ATTRIBUTE.containsKey(parsed);
    }
}
