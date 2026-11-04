package com.sofodev.thedragonlib.client.config;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Field layout of one config list entry (e.g. {@code "entry_id|other_id|1|0.05"}): split/join/validate/describe a line
 * against a fixed set of typed {@link Field}s. The line syntax comes from its {@link RuleFormat}.
 *
 * @author Sokratis Fotkatzikis
 */
public record RuleType(String key, List<Field> fields, int minIndex, int maxIndex, RuleFormat format) {

    public RuleType(String key, List<Field> fields, int minIndex, int maxIndex) {
        this(key, fields, minIndex, maxIndex, RuleFormat.PIPE);
    }

    public RuleType(String key, List<Field> fields, RuleFormat format) {
        this(key, fields, -1, -1, format);
    }

    public RuleType(String key, List<Field> fields) {
        this(key, fields, RuleFormat.PIPE);
    }

    /**
     * {@code ATTRIBUTE_VALUE} is a decimal whose range comes from the attribute chosen in the same line. {@code CUSTOM} is a
     * picker whose options the {@link RuleSuggestionSource} provides by {@link Field#source()}. {@code CHOICE} and
     * {@code TOGGLE} are buttons instead of text inputs.
     */
    public enum Kind { ENTITY, SPAWNABLE_ENTITY, ITEM, BLOCK, BIOME, ATTRIBUTE, CUSTOM, INTEGER, DECIMAL, ATTRIBUTE_VALUE, CHOICE, TOGGLE }

    /**
     * Decimal fields with {@code percent} set are entered as 0-1 but read better as 0-100%.
     *
     * @param source  what a {@code CUSTOM} picker lists, empty otherwise
     * @param choices the values of a {@code CHOICE} button, empty otherwise
     */
    public record Field(String name, Kind kind, boolean wildcard, boolean optional, double min, double max, boolean percent,
                        String source, List<String> choices) {
        public boolean isPicker() { return !isNumber() && !isButton(); }

        public boolean isNumber() { return kind == Kind.INTEGER || kind == Kind.DECIMAL || kind == Kind.ATTRIBUTE_VALUE; }

        public boolean isButton() { return kind == Kind.CHOICE || kind == Kind.TOGGLE; }

        public Field asOptional() {
            return new Field(name, kind, wildcard, true, min, max, percent, source, choices);
        }
    }

    public static Field picker(String name, Kind kind, boolean wildcard) {
        return new Field(name, kind, wildcard, false, 0, 0, false, "", List.of());
    }

    public static Field custom(String name, String source) {
        return new Field(name, Kind.CUSTOM, false, false, 0, 0, false, source, List.of());
    }

    public static Field integer(String name, double min, double max) {
        return new Field(name, Kind.INTEGER, false, false, min, max, false, "", List.of());
    }

    public static Field decimal(String name, double min, double max, boolean optional) {
        return new Field(name, Kind.DECIMAL, false, optional, min, max, false, "", List.of());
    }

    public static Field chance(boolean optional) {
        return new Field("chance", Kind.DECIMAL, false, optional, 0, 1, true, "", List.of());
    }

    public static Field toggle(String name) {
        return new Field(name, Kind.TOGGLE, false, false, 0, 0, false, "", List.of());
    }

    public static Field choice(String name, String... choices) {
        return new Field(name, Kind.CHOICE, false, false, 0, 0, false, "", List.of(choices));
    }

    public String[] split(String line) {
        return format.split(line, this);
    }

    public String join(String[] values) {
        return format.join(values, this);
    }

    /** Allowed {min, max} for a number field, or null when it has no meaningful bounds yet. */
    public double @Nullable [] range(int index, String[] values, RuleSuggestionSource suggestions) {
        Field field = fields.get(index);
        if (field.kind() == Kind.ATTRIBUTE_VALUE) return suggestions.attributeRange(attributeValue(values));
        return field.isNumber() ? new double[] {field.min(), field.max()} : null;
    }

    String attributeValue(String[] values) {
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).kind() == Kind.ATTRIBUTE) return values[i];
        }
        return "";
    }

    /** Returns null when valid, otherwise a translation key describing the first problem. */
    public @Nullable String validate(String line, RuleListContext ctx) {
        if (!format.matches(line, this)) return ctx.uiKey("error.fieldCount");
        String[] values = split(line);
        for (int i = 0; i < values.length; i++) {
            String error = validateField(i, values, ctx);
            if (error != null) return error;
        }
        if (minIndex >= 0 && !values[minIndex].isEmpty() && !values[maxIndex].isEmpty()
                && Double.parseDouble(values[maxIndex]) < Double.parseDouble(values[minIndex])) {
            return ctx.uiKey("error.maxBelowMin");
        }
        return format.validate(values, this, ctx);
    }

    public @Nullable String validateField(int index, String[] values, RuleListContext ctx) {
        Field field = fields.get(index);
        String value = values[index];
        if (value.isEmpty()) return field.optional() || field.kind() == Kind.TOGGLE ? null : ctx.uiKey("error.required");
        if (field.kind() == Kind.TOGGLE) {
            return value.equals("true") || value.equals("false") ? null : ctx.uiKey("error.unknownId");
        }
        if (field.kind() == Kind.CHOICE) return field.choices().contains(value) ? null : ctx.uiKey("error.unknownId");
        if (field.isPicker()) {
            if (value.equals("*")) return field.wildcard() ? null : ctx.uiKey("error.noWildcard");
            return ctx.suggestions().isKnown(field, value, values) ? null : ctx.uiKey("error.unknownId");
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
        Component custom = format.describe(values, this, ctx);
        if (custom != null) return custom;
        Object[] args = new Object[values.length];
        boolean hasOptional = false;
        for (int i = 0; i < values.length; i++) {
            Field field = fields.get(i);
            if (field.optional() && !values[i].isEmpty()) hasOptional = true;
            args[i] = field.isPicker() ? ctx.suggestions().displayName(field, values[i])
                    : field.isButton() ? buttonLabel(field, values[i], ctx)
                    : field.percent() && !values[i].isEmpty() ? format(Double.parseDouble(values[i]) * 100) + "%"
                    : values[i];
        }
        return Component.translatable(ctx.uiKey("describe." + key + (hasOptional ? ".optional" : "")), args);
    }

    /** What a {@code CHOICE} or {@code TOGGLE} value reads as; choices use {@code choice.<field>.<value>} if translated. */
    public static Component buttonLabel(Field field, String value, RuleListContext ctx) {
        if (field.kind() == Kind.TOGGLE) return Component.translatable(value.equals("true") ? "options.on" : "options.off");
        String translation = ctx.uiKey("choice." + field.name() + "." + (value.isEmpty() ? "none" : value));
        return I18n.exists(translation) ? Component.translatable(translation) : Component.literal(value);
    }

    public static String format(double value) {
        if (value == Math.rint(value) && Math.abs(value) < 1e15) return String.valueOf((long) value);
        return String.valueOf(Math.round(value * 10000) / 10000.0);
    }
}
