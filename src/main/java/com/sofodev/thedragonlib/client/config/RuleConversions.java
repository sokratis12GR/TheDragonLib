package com.sofodev.thedragonlib.client.config;

import org.jetbrains.annotations.Nullable;

/**
 * Lets {@link RuleListScreen} paste lines copied from another rule list: they're converted field by field (by name), and
 * required values the source doesn't have come from {@link #defaultValue}.
 *
 * @author Sokratis Fotkatzikis
 */
public interface RuleConversions {
    /** The rule type with this config key, or null if there's none. */
    @Nullable RuleType type(String key);

    /** What to fill in for a field the copied line didn't have, or null to leave it empty (optional fields then use their fallback). */
    default @Nullable String defaultValue(RuleType type, RuleType.Field field) {
        return null;
    }
}
