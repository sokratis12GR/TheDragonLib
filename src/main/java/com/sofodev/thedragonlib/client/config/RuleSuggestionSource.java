package com.sofodev.thedragonlib.client.config;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Supplies the searchable id/name options a {@link RuleListScreen}/{@link RuleEditScreen} picker field
 * offers, plus the lookups {@link RuleType} needs to validate and describe a line. Implemented by the
 * caller, since what counts as a valid entity/item/attribute/biome id is mod-specific.
 *
 * @author Sokratis Fotkatzikis
 */
public interface RuleSuggestionSource {

    record Option(String id, String name) {
        public boolean matches(String query) {
            return query.isEmpty() || id.contains(query) || name.toLowerCase(Locale.ROOT).contains(query);
        }
    }

    List<Option> options(RuleType.Kind kind, @Nullable String context);

    boolean isKnown(RuleType.Kind kind, String id);

    String displayName(RuleType.Kind kind, String id);

    Component anyLabel(RuleType.Kind kind);

    @Nullable Component help(RuleType.Kind kind, String id);

    /** Allowed {min, max} of an attribute id, or null if it's unknown or unbounded. Only used for {@code ATTRIBUTE}/{@code ATTRIBUTE_VALUE} kinds. */
    double @Nullable [] attributeRange(String attributeId);

    /** The context's normal value for an attribute, falling back to the attribute's own default. Only used for {@code ATTRIBUTE_VALUE} kinds. */
    @Nullable Double attributeDefault(@Nullable String context, String attributeId);
}
