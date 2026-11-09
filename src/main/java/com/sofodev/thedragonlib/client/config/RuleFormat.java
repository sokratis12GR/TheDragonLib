package com.sofodev.thedragonlib.client.config;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * How one line of a config list maps to the {@link RuleType.Field}s of its {@link RuleType}. {@link #PIPE} covers plain
 * positional {@code "a|b|c"} lines; a mod with another syntax (prefixes, {@code key=value} pairs, ...) or lines saved in an
 * older layout implements its own.
 *
 * @author Sokratis Fotkatzikis
 */
public interface RuleFormat {

    /** Positional, pipe-delimited values; trailing optional fields can be left out. */
    RuleFormat PIPE = new RuleFormat() {
        @Override
        public boolean matches(String line, RuleType type) {
            int count = line.split("\\|", -1).length;
            long required = type.fields().stream().filter(f -> !f.optional()).count();
            return count >= required && count <= type.fields().size();
        }

        @Override
        public String[] split(String line, RuleType type) {
            String[] parts = line.split("\\|", -1);
            String[] values = new String[type.fields().size()];
            for (int i = 0; i < values.length; i++) values[i] = i < parts.length ? parts[i].trim() : "";
            return values;
        }

        @Override
        public String join(String[] values, RuleType type) {
            List<String> parts = new ArrayList<>(List.of(values));
            while (!parts.isEmpty() && parts.getLast().isEmpty() && type.fields().get(parts.size() - 1).optional()) parts.removeLast();
            return String.join("|", parts);
        }
    };

    /** Whether the line has the right structure; the values themselves are checked per field afterwards. */
    boolean matches(String line, RuleType type);

    /** One value per field, empty for those the line leaves out. Must not fail on malformed lines, the editor opens those too. */
    String[] split(String line, RuleType type);

    String join(String[] values, RuleType type);

    /** Checks across fields once each field is valid; returns a translation key describing the problem, or null. */
    default @Nullable String validate(String[] values, RuleType type, RuleListContext ctx) {
        return null;
    }

    /** A readable summary of a valid line, or null to use the {@code describe.<key>} translation with one argument per field. */
    default @Nullable Component describe(String[] values, RuleType type, RuleListContext ctx) {
        return null;
    }
}
