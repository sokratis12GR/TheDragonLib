package com.sofodev.thedragonlib.client.config;

/**
 * Caller-supplied wiring for {@link RuleListScreen}/{@link RuleEditScreen}/{@link RuleType}:
 * translation key prefixes plus the id lookups in {@link RuleSuggestionSource}.
 *
 * @param uiKeyPrefix     prefix for generic UI strings, e.g. {@code "mymod.ui."}
 * @param configKeyPrefix prefix for per-rule-type title/tooltip strings, e.g. {@code "mymod.configuration."}
 *
 * @author Sokratis Fotkatzikis
 */
public record RuleListContext(String uiKeyPrefix, String configKeyPrefix, RuleSuggestionSource suggestions) {

    public String uiKey(String suffix) {
        return uiKeyPrefix + suffix;
    }

    public String configKey(String suffix) {
        return configKeyPrefix + suffix;
    }
}
