package com.sofodev.thedragonlib.client.config;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Supplies the searchable id/name options a {@link RuleListScreen}/{@link RuleEditScreen} picker field
 * offers, plus the lookups {@link RuleType} needs to validate and describe a line. {@link RegistrySuggestionSource}
 * covers the registry kinds; a mod extends it for its {@code CUSTOM} fields.
 *
 * @author Sokratis Fotkatzikis
 */
public interface RuleSuggestionSource {

    record Option(String id, String name, ItemStack icon) {
        public Option(String id, String name) {
            this(id, name, ItemStack.EMPTY);
        }

        public boolean matches(String query) {
            return query.isEmpty() || id.contains(query) || name.toLowerCase(Locale.ROOT).contains(query);
        }
    }

    /** @param values the current values of the whole line, since what a field offers can depend on another field */
    List<Option> options(RuleType.Field field, String[] values);

    /** @param values the current values of the whole line, since what a field accepts can depend on another field */
    boolean isKnown(RuleType.Field field, String id, String[] values);

    String displayName(RuleType.Field field, String id);

    Component anyLabel(RuleType.Field field);

    @Nullable Component help(RuleType.Field field, String id);

    /** Allowed {min, max} of an attribute id, or null if it's unknown or unbounded. Only used for {@code ATTRIBUTE}/{@code ATTRIBUTE_VALUE} kinds. */
    double @Nullable [] attributeRange(String attributeId);

    /** The context's normal value for an attribute, falling back to the attribute's own default. Only used for {@code ATTRIBUTE_VALUE} kinds. */
    @Nullable Double attributeDefault(@Nullable String context, String attributeId);
}
