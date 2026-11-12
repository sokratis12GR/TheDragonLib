package com.sofodev.thedragonlib.client.config;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clipboard text for the rule screens. Copies start with a {@code # <mod>:<key>} header naming the list they came from,
 * so pasting into another list can convert them. Pasting also accepts lines copied straight from the .toml file.
 *
 * @author Sokratis Fotkatzikis
 */
final class RuleClipboard {
    private static final Pattern HEADER = Pattern.compile("^#\\s*([a-z0-9_.-]+):([A-Za-z0-9_]+)\\s*$");
    private static final Pattern QUOTED = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"|'([^']*)'");
    private static final Pattern TOML_KEY = Pattern.compile("^[A-Za-z0-9_.-]+\\s*=\\s*");

    private RuleClipboard() {}

    /** @param sourceKey the list the lines were copied from, null without a header from this mod */
    record Parsed(@Nullable String sourceKey, List<String> lines) {}

    static String namespace(RuleListContext ctx) {
        String prefix = ctx.uiKeyPrefix();
        int dot = prefix.indexOf('.');
        return dot > 0 ? prefix.substring(0, dot) : prefix;
    }

    static String header(RuleListContext ctx, RuleType type) {
        return "# " + namespace(ctx) + ":" + type.key();
    }

    static String write(RuleListContext ctx, RuleType type, List<String> lines) {
        return header(ctx, type) + "\n" + String.join("\n", lines);
    }

    static Parsed parse(RuleListContext ctx, String text) {
        String source = null;
        List<String> lines = new ArrayList<>();
        for (String raw : text.split("\\R")) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("#")) {
                Matcher header = HEADER.matcher(line);
                if (source == null && header.matches() && header.group(1).equals(namespace(ctx))) source = header.group(2);
                continue;
            }
            // From the .toml: key = ["a", "b"], one quoted value per line with commas, or the closing bracket.
            Matcher key = TOML_KEY.matcher(line);
            boolean toml = key.lookingAt() || line.startsWith("\"") || line.startsWith("'") || line.startsWith("[") || line.equals("]");
            if (!toml) {
                lines.add(line.endsWith(",") ? line.substring(0, line.length() - 1).trim() : line);
                continue;
            }
            Matcher quoted = QUOTED.matcher(line);
            while (quoted.find()) {
                String value = quoted.group(1) != null ? quoted.group(1).replace("\\\"", "\"").replace("\\\\", "\\") : quoted.group(2);
                if (!value.isBlank()) lines.add(value.trim());
            }
        }
        return new Parsed(source, lines);
    }

    /**
     * The line rewritten for another rule type: pickers (entity, item, biome, dimension...) carry over by field name,
     * numbers and toggles don't, and values left empty come from the conversions' defaults.
     */
    static String convert(String line, RuleType from, RuleType to, RuleConversions conversions) {
        String[] source = from.split(line);
        String[] target = new String[to.fields().size()];
        for (int i = 0; i < target.length; i++) {
            RuleType.Field field = to.fields().get(i);
            target[i] = "";
            if (field.isPicker()) {
                for (int j = 0; j < from.fields().size() && j < source.length; j++) {
                    RuleType.Field other = from.fields().get(j);
                    if (other.name().equals(field.name()) && other.isPicker()) target[i] = source[j];
                }
            }
            if (target[i].isEmpty()) {
                String fallback = conversions.defaultValue(to, field);
                if (fallback != null) target[i] = fallback;
            }
        }
        return to.join(target);
    }
}
