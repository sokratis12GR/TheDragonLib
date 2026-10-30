package com.sofodev.thedragonlib.client.config;

import com.sofodev.thedragonlib.client.config.EntryPickerScreen.Candidate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Turns a built-in or datapack registry into a searchable {@link Candidate} list for
 * {@link EntryPickerScreen}. For datapack registries (biomes, enchantments) that have no in-memory
 * representation outside a loaded world, falls back to reflecting over the vanilla constants class
 * ({@link Biomes}, {@link Enchantments}) so the picker still has something to show from the title screen.
 *
 * @author Sokratis Fotkatzikis
 */
public final class RegistryCandidates {

    public enum Source {
        ENTITY, SPAWNABLE_ENTITY, ITEM, BLOCK, ATTRIBUTE, ENCHANTMENT, BIOME
    }

    private static final Map<Source, List<Candidate>> STATIC_CACHE = new EnumMap<>(Source.class);
    private static List<Candidate> vanillaBiomes;
    private static List<Candidate> vanillaEnchantments;

    private RegistryCandidates() {
    }

    public static List<Candidate> of(Source source) {
        return switch (source) {
            case BIOME -> biomeCandidates();
            case ENCHANTMENT -> enchantmentCandidates();
            default -> STATIC_CACHE.computeIfAbsent(source, RegistryCandidates::collect);
        };
    }

    public static boolean isKnown(Source source, String id) {
        Identifier parsed = Identifier.tryParse(id);
        if (parsed == null) return false;
        return switch (source) {
            case ENTITY, SPAWNABLE_ENTITY -> of(source).stream().anyMatch(c -> c.id().equals(parsed.toString()));
            case ITEM -> BuiltInRegistries.ITEM.containsKey(parsed);
            case BLOCK -> BuiltInRegistries.BLOCK.containsKey(parsed);
            case ATTRIBUTE -> BuiltInRegistries.ATTRIBUTE.containsKey(parsed);
            // Biomes and enchantments are datapack-driven, so modded ones can't be verified without a loaded world.
            case BIOME, ENCHANTMENT -> true;
        };
    }

    public static Component displayName(Source source, String id) {
        return of(source).stream().filter(c -> c.id().equals(id)).findFirst()
                .map(Candidate::name).orElseGet(() -> Component.literal(id));
    }

    private static List<Candidate> collect(Source source) {
        return switch (source) {
            case ENTITY -> entityCandidates(false);
            case SPAWNABLE_ENTITY -> entityCandidates(true);
            case ITEM -> itemCandidates();
            case BLOCK -> blockCandidates();
            case ATTRIBUTE -> attributeCandidates();
            case BIOME, ENCHANTMENT -> throw new IllegalStateException("handled separately");
        };
    }

    private static List<Candidate> entityCandidates(boolean spawnableOnly) {
        return sorted(BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(DefaultAttributes::hasSupplier)
                .filter(type -> !spawnableOnly || type.getCategory() != MobCategory.MISC)
                .map(type -> new Candidate(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString(),
                        ItemStack.EMPTY, type.getDescription(), type)));
    }

    private static List<Candidate> itemCandidates() {
        return sorted(BuiltInRegistries.ITEM.stream()
                .filter(item -> item != Items.AIR)
                .map(item -> new Candidate(BuiltInRegistries.ITEM.getKey(item).toString(),
                        icon(item), Component.translatable(item.getDescriptionId()), item)));
    }

    private static List<Candidate> blockCandidates() {
        return sorted(BuiltInRegistries.BLOCK.stream()
                .filter(block -> !block.defaultBlockState().isAir())
                .map(block -> new Candidate(BuiltInRegistries.BLOCK.getKey(block).toString(),
                        icon(block.asItem()), block.getName(), block)));
    }

    private static List<Candidate> attributeCandidates() {
        return sorted(BuiltInRegistries.ATTRIBUTE.stream()
                .map(attribute -> new Candidate(BuiltInRegistries.ATTRIBUTE.getKey(attribute).toString(),
                        ItemStack.EMPTY, Component.translatable(attribute.getDescriptionId()), attribute)));
    }

    private static List<Candidate> biomeCandidates() {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            ItemStack icon = icon(Items.GRASS_BLOCK);
            return sorted(level.registryAccess().lookupOrThrow(Registries.BIOME).listElementIds()
                    .map(key -> biomeCandidate(key.identifier(), icon)));
        }
        if (vanillaBiomes == null) {
            vanillaBiomes = reflectConstants(Biomes.class, icon(Items.GRASS_BLOCK), RegistryCandidates::biomeCandidate);
        }
        return vanillaBiomes;
    }

    private static Candidate biomeCandidate(Identifier id, ItemStack icon) {
        return new Candidate(id.toString(), icon,
                Component.translatable("biome." + id.getNamespace() + "." + id.getPath()), id);
    }

    private static List<Candidate> enchantmentCandidates() {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            ItemStack icon = icon(Items.ENCHANTED_BOOK);
            return sorted(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements()
                    .map(holder -> new Candidate(holder.key().identifier().toString(), icon, holder.value().description(), holder.value())));
        }
        if (vanillaEnchantments == null) {
            vanillaEnchantments = reflectConstants(Enchantments.class, icon(Items.ENCHANTED_BOOK), RegistryCandidates::enchantmentCandidate);
        }
        return vanillaEnchantments;
    }

    private static Candidate enchantmentCandidate(Identifier id, ItemStack icon) {
        return new Candidate(id.toString(), icon,
                Component.translatable("enchantment." + id.getNamespace() + "." + id.getPath()), id);
    }

    /** Reflects over a vanilla constants class' static {@code ResourceKey} fields, for datapack registries with no loaded world. */
    private static List<Candidate> reflectConstants(Class<?> constantsClass, ItemStack icon,
            java.util.function.BiFunction<Identifier, ItemStack, Candidate> factory) {
        List<Candidate> found = new ArrayList<>();
        for (Field field : constantsClass.getFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || field.getType() != ResourceKey.class) continue;
            try {
                found.add(factory.apply(((ResourceKey<?>) field.get(null)).identifier(), icon));
            } catch (IllegalAccessException ignored) {
            }
        }
        return sorted(found.stream());
    }

    private static ItemStack icon(Item item) {
        return item.builtInRegistryHolder().areComponentsBound() ? new ItemStack(item) : ItemStack.EMPTY;
    }

    private static List<Candidate> sorted(Stream<Candidate> candidates) {
        return candidates.sorted(Comparator.comparing(Candidate::id)).toList();
    }
}
