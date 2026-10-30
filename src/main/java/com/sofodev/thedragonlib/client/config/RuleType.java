package com.sofodev.thedragonlib.client.config;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Field layout of one pipe-delimited config list entry (e.g. {@code "entry_id|other_id|1|0.05"}):
 * split/join/validate/describe a line against a fixed set of typed {@link Field}s.
 *
 * @author Sokratis Fotkatzikis
 */
public record RuleType(String key, List<Field> fields, int minIndex, int maxIndex) {

    /** {@code ATTRIBUTE_VALUE} is a decimal whose range comes from the attribute chosen in the same line. */
    public enum Kind { ENTITY, SPAWNABLE_ENTITY, ITEM, BIOME, ATTRIBUTE, INTEGER, DECIMAL, ATTRIBUTE_VALUE }

    /** Decimal fields with {@code percent} set are entered as 0-1 but read better as 0-100%. */
    public record Field(String name, Kind kind, boolean wildcard, boolean optional, double min, double max, boolean percent) {
        public boolean isPicker() { return kind != Kind.INTEGER && kind != Kind.DECIMAL && kind != Kind.ATTRIBUTE_VALUE; }
    }

    public static Field picker(String name, Kind kind, boolean wildcard) {
        return new Field(name, kind, wildcard, false, 0, 0, false);
    }

    public static Field integer(String name, double min, double max) {
        return new Field(name, Kind.INTEGER, false, false, min, max, false);
    }

    public static Field decimal(String name, double min, double max, boolean optional) {
        return new Field(name, Kind.DECIMAL, false, optional, min, max, false);
    }

    public static Field chance(boolean optional) {
        return new Field("chance", Kind.DECIMAL, false, optional, 0, 1, true);
    }

    public String[] split(String line) {
        String[] parts = line.split("\\|", -1);
        String[] values = new String[fields.size()];
        for (int i = 0; i < values.length; i++) values[i] = i < parts.length ? parts[i].trim() : "";
        return values;
    }

    public String join(String[] values) {
        List<String> parts = new ArrayList<>(List.of(values));
        while (!parts.isEmpty() && parts.getLast().isEmpty() && fields.get(parts.size() - 1).optional()) parts.removeLast();
        return String.join("|", parts);
    }

    /** Allowed {min, max} for a number field, or null when it has no meaningful bounds yet. */
    public double @Nullable [] range(int index, String[] values, RuleSuggestionSource suggestions) {
        Field field = fields.get(index);
        if (field.kind() == Kind.ATTRIBUTE_VALUE) return suggestions.attributeRange(attributeValue(values));
        return field.isPicker() ? null : new double[] {field.min(), field.max()};
    }

    private String attributeValue(String[] values) {
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).kind() == Kind.ATTRIBUTE) return values[i];
        }
        return "";
    }

    /** Returns null when valid, otherwise a translation key describing the first problem. */
    public @Nullable String validate(String line, RuleListContext ctx) {
        String[] raw = line.split("\\|", -1);
        long required = fields.stream().filter(f -> !f.optional()).count();
        if (raw.length < required || raw.length > fields.size()) return ctx.uiKey("error.fieldCount");
        String[] values = split(line);
        for (int i = 0; i < values.length; i++) {
            String error = validateField(i, values, ctx);
            if (error != null) return error;
        }
        if (minIndex >= 0 && Double.parseDouble(values[maxIndex]) < Double.parseDouble(values[minIndex])) {
            return ctx.uiKey("error.maxBelowMin");
        }
        return null;
    }

    public @Nullable String validateField(int index, String[] values, RuleListContext ctx) {
        Field field = fields.get(index);
        String value = values[index];
        if (value.isEmpty()) return field.optional() ? null : ctx.uiKey("error.required");
        if (field.isPicker()) {
            if (value.equals("*")) return field.wildcard() ? null : ctx.uiKey("error.noWildcard");
            return ctx.suggestions().isKnown(field.kind(), value) ? null : ctx.uiKey("error.unknownId");
        }
        final double number;
        try {
            number = field.kind() == Kind.INTEGER ? Integer.parseInt(value) : Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return ctx.uiKey("error.notNumber");
        }
        double[] range = range(index, values, ctx.suggestions());
        if (!Double.isFinite(number) || (range != null && (number < range[0] || number > range[1]))) {
            return ctx.uiKey("error.outOfRange");
        }
        return null;
    }

    /** A readable sentence for a line, e.g. "Zombie, Diamond, amount 1 (5% chance)"; null if the line is invalid. */
    public @Nullable Component describe(String line, RuleListContext ctx) {
        if (validate(line, ctx) != null) return null;
        String[] values = split(line);
        Object[] args = new Object[values.length];
        boolean hasOptional = false;
        for (int i = 0; i < values.length; i++) {
            Field field = fields.get(i);
            if (field.optional() && !values[i].isEmpty()) hasOptional = true;
            args[i] = field.isPicker() ? ctx.suggestions().displayName(field.kind(), values[i])
                    : field.percent() && !values[i].isEmpty() ? format(Double.parseDouble(values[i]) * 100) + "%"
                    : values[i];
        }
        return Component.translatable(ctx.uiKey("describe." + key + (hasOptional ? ".optional" : "")), args);
    }

    public static String format(double value) {
        if (value == Math.rint(value) && Math.abs(value) < 1e15) return String.valueOf((long) value);
        return String.valueOf(Math.round(value * 10000) / 10000.0);
    }
}
