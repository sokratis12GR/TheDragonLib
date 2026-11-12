package com.sofodev.thedragonlib.client.config;

import org.jetbrains.annotations.Nullable;

/**
 * Caller-supplied wiring for {@link RuleListScreen}/{@link RuleEditScreen}/{@link RuleType}:
 * translation key prefixes plus the id lookups in {@link RuleSuggestionSource}.
 *
 * @param uiKeyPrefix     prefix for generic UI strings, e.g. {@code "mymod.ui."}
 * @param configKeyPrefix prefix for per-rule-type title/tooltip strings, e.g. {@code "mymod.configuration."}
 * @param extension       extra data some picker fields carry after their id, null if none do
 *
 * @author Sokratis Fotkatzikis
 */
public record RuleListContext(String uiKeyPrefix, String configKeyPrefix, RuleSuggestionSource suggestions,
                              @Nullable FieldExtension extension) {

    public RuleListContext(String uiKeyPrefix, String configKeyPrefix, RuleSuggestionSource suggestions) {
        this(uiKeyPrefix, configKeyPrefix, suggestions, null);
    }

    /** The extension if it applies to this field of the rule type, otherwise null. */
    public @Nullable FieldExtension extension(RuleType type, int field) {
        return extension != null && extension.appliesTo(type, field) ? extension : null;
    }

    public String uiKey(String suffix) {
        return uiKeyPrefix + suffix;
    }

    public String configKey(String suffix) {
        return configKeyPrefix + suffix;
    }
}
