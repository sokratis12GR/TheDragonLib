package com.sofodev.thedragonlib.client.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Edits a single rule line with one input per field and a searchable list of valid IDs.
 *
 * @author Sokratis Fotkatzikis
 */
final class RuleEditScreen extends Screen {
    private static final int TOP = 44;
    private static final int ROW_HEIGHT = 34;
    private static final int LABEL_X = 20;
    private static final int BOX_X = LABEL_X + 90;

    private final Screen parent;
    private final RuleType type;
    private final RuleListContext ctx;
    private final String[] values;
    private final Consumer<String> onSave;
    /** One per field, null for the fields that use a button. */
    private final EditBox[] boxes;
    private int activeField = -1;
    private @Nullable SuggestionList suggestions;
    private @Nullable Button saveButton;
    private @Nullable String error;

    RuleEditScreen(Screen parent, RuleType type, RuleListContext ctx, String line, Consumer<String> onSave) {
        super(Component.translatable(ctx.uiKey("editTitle"), Component.translatable(ctx.configKey(type.key()))));
        this.parent = parent;
        this.type = type;
        this.ctx = ctx;
        this.values = type.split(line);
        this.boxes = new EditBox[values.length];
        this.onSave = onSave;
        for (int i = 0; i < values.length; i++) {
            RuleType.Field field = type.fields().get(i);
            if (values[i].isEmpty() && field.kind() == RuleType.Kind.CHOICE && !field.optional()) values[i] = field.choices().getFirst();
            if (values[i].isEmpty() && field.kind() == RuleType.Kind.TOGGLE) values[i] = "false";
        }
    }

    @Override
    protected void init() {
        int boxWidth = Math.max(80, width / 2 - BOX_X - 10);
        int rowHeight = rowHeight();
        for (int i = 0; i < type.fields().size(); i++) {
            RuleType.Field field = type.fields().get(i);
            int index = i;
            if (field.isButton()) {
                boxes[i] = null;
                addRenderableWidget(Button.builder(RuleType.buttonLabel(field, values[i], ctx), b -> cycle(index, b))
                        .bounds(BOX_X, TOP + i * rowHeight, boxWidth, 20)
                        .tooltip(Tooltip.create(help(field))).build());
                continue;
            }
            EditBox box = new EditBox(font, BOX_X, TOP + i * rowHeight, boxWidth, 20, fieldLabel(field));
            box.setMaxLength(256);
            box.setValue(values[i]);
            box.setHint(hint(field));
            box.setTooltip(Tooltip.create(help(field)));
            box.setResponder(value -> {
                values[index] = value.trim();
                // What a picker offers can depend on the line's first field, so edits there refresh it too.
                if (index == activeField || index == 0) refreshSuggestions();
                revalidate();
            });
            boxes[i] = addRenderableWidget(box);
        }

        int listX = width / 2 + 10;
        suggestions = addRenderableWidget(new SuggestionList(minecraft, width - listX - 20, height - 52 - 40, 52));
        suggestions.setX(listX);

        saveButton = addRenderableWidget(Button.builder(Component.translatable(ctx.uiKey("save")), b -> save())
                .bounds(width / 2 - 154, height - 28, 150, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .bounds(width / 2 + 4, height - 28, 150, 20).build());

        int first = activeField >= 0 ? activeField : nextBox(0);
        activeField = -1;
        if (first >= 0) {
            setActiveField(type.fields().get(first).isPicker() ? first : -1);
            setInitialFocus(boxes[first]);
        }
        revalidate();
    }

    private int nextBox(int from) {
        for (int i = from; i < boxes.length; i++) {
            if (boxes[i] != null) return i;
        }
        return -1;
    }

    /** Rows shrink on short screens so rules with many fields stay above the error line and buttons. */
    private int rowHeight() {
        return Math.max(22, Math.min(ROW_HEIGHT, (height - TOP - 44) / type.fields().size()));
    }

    private void cycle(int index, Button button) {
        RuleType.Field field = type.fields().get(index);
        if (field.kind() == RuleType.Kind.TOGGLE) {
            values[index] = values[index].equals("true") ? "false" : "true";
        } else {
            List<String> choices = new ArrayList<>(field.choices());
            if (field.optional()) choices.add("");
            values[index] = choices.get((choices.indexOf(values[index]) + 1) % choices.size());
        }
        button.setMessage(RuleType.buttonLabel(field, values[index], ctx));
        if (index == 0) refreshSuggestions();
        revalidate();
    }

    private Component fieldLabel(RuleType.Field field) {
        return Component.translatable(ctx.uiKey("field." + field.name()));
    }

    /** Per-rule-type help when one exists for this field, otherwise the shared help for that field. */
    private Component help(RuleType.Field field) {
        String specific = ctx.uiKey("help." + type.key() + "." + field.name());
        return Component.translatable(I18n.exists(specific) ? specific : ctx.uiKey("help." + field.name()));
    }

    private Component hint(RuleType.Field field) {
        String key = field.isPicker() && field.wildcard() ? ctx.uiKey("hint.wildcard")
                : field.optional() ? ctx.uiKey("hint.optional")
                : null;
        return key == null ? Component.empty() : Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY);
    }

    /** Small line under a field: the picked thing's name, or the allowed range (and the context's default for attributes). */
    private @Nullable Component note(int index) {
        RuleType.Field field = type.fields().get(index);
        String value = values[index];
        if (field.isButton()) return null;
        if (field.isPicker()) {
            if (value.isEmpty() || type.validateField(index, values, ctx) != null) return null;
            String name = ctx.suggestions().displayName(field, value);
            return name.equals(value) ? null : Component.literal(name);
        }
        double[] range = type.range(index, values, ctx.suggestions());
        if (field.kind() == RuleType.Kind.ATTRIBUTE_VALUE) {
            Double defaultValue = ctx.suggestions().attributeDefault(values[0], type.attributeValue(values));
            if (defaultValue == null) return Component.translatable(ctx.uiKey("note.pickAttribute"));
            Component normal = Component.translatable(ctx.uiKey("note.default"), RuleType.format(defaultValue));
            return range == null ? normal
                    : Component.translatable(ctx.uiKey("note.rangeDefault"), rangeText(range, false), normal);
        }
        return range == null ? null : rangeText(range, field.percent());
    }

    private Component rangeText(double[] range, boolean percent) {
        Component text = Double.isInfinite(range[1])
                ? Component.translatable(ctx.uiKey("note.atLeast"), RuleType.format(range[0]))
                : Component.translatable(ctx.uiKey("note.range"), RuleType.format(range[0]), RuleType.format(range[1]));
        return percent ? Component.translatable(ctx.uiKey("note.percent"), text) : text;
    }

    private void setActiveField(int index) {
        if (index == activeField && index >= 0) return;
        activeField = index;
        refreshSuggestions();
    }

    private void refreshSuggestions() {
        if (suggestions == null) return;
        if (activeField < 0 || !type.fields().get(activeField).isPicker()) {
            suggestions.show(null, List.of());
            return;
        }
        RuleType.Field field = type.fields().get(activeField);
        String query = values[activeField].toLowerCase(Locale.ROOT);
        List<RuleSuggestionSource.Option> matches = new ArrayList<>();
        if (field.wildcard() && "*".contains(query)) {
            matches.add(new RuleSuggestionSource.Option("*", ctx.suggestions().anyLabel(field).getString()));
        }
        for (RuleSuggestionSource.Option option : ctx.suggestions().options(field, values)) {
            if (option.matches(query)) matches.add(option);
        }
        suggestions.show(field, matches);
    }

    private void pick(RuleSuggestionSource.Option option) {
        if (activeField < 0) return;
        EditBox box = boxes[activeField];
        box.setValue(option.id());
        int next = nextBox(activeField + 1);
        if (next >= 0) {
            setFocused(boxes[next]);
            setActiveField(type.fields().get(next).isPicker() ? next : activeField);
        } else {
            setFocused(box);
        }
    }

    private void revalidate() {
        error = null;
        for (int i = 0; i < values.length && error == null; i++) {
            String fieldError = type.validateField(i, values, ctx);
            if (fieldError != null) {
                error = Component.translatable(fieldError).getString() + " (" + fieldLabel(type.fields().get(i)).getString() + ")";
            }
        }
        if (error == null) {
            String lineError = type.validate(type.join(values), ctx);
            if (lineError != null) error = Component.translatable(lineError).getString();
        }
        if (saveButton != null) saveButton.active = error == null;
    }

    private void save() {
        if (error != null) return;
        onSave.accept(type.join(values));
        minecraft.setScreen(parent);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void tick() {
        // The suggestion list takes focus when clicked, so only switch fields when another box is focused.
        if (getFocused() instanceof EditBox focused) {
            int index = Arrays.asList(boxes).indexOf(focused);
            if (index >= 0 && type.fields().get(index).isPicker()) setActiveField(index);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(font, title, width / 2, 10, 0xFFFFFFFF);
        RuleListScreen.subtitle(graphics, font, Component.translatable(ctx.configKey(type.key() + ".tooltip")), width, 22, mouseX, mouseY);
        int rowHeight = rowHeight();
        // The note sits under the box; on very short screens there's no room for it.
        int noteOffset = Math.min(23, rowHeight - font.lineHeight);
        int boxWidth = Math.max(80, width / 2 - BOX_X - 10);
        for (int i = 0; i < type.fields().size(); i++) {
            RuleType.Field field = type.fields().get(i);
            int rowY = TOP + i * rowHeight;
            int color = i == activeField ? 0xFFFFFF55 : 0xFFE0E0E0;
            graphics.text(font, fieldLabel(field), LABEL_X, rowY + 6, color);
            Component note = noteOffset > 20 ? note(i) : null;
            if (note != null) {
                graphics.text(font, font.plainSubstrByWidth(note.getString(), boxWidth), BOX_X + 2, rowY + noteOffset, 0xFF909090);
            }
            if (mouseX >= LABEL_X && mouseX < BOX_X && mouseY >= rowY && mouseY < rowY + 20) {
                graphics.setTooltipForNextFrame(font.split(help(field), 200), mouseX, mouseY);
            }
        }
        Component header = activeField >= 0
                ? Component.translatable(ctx.uiKey("matches"), fieldLabel(type.fields().get(activeField)))
                : Component.translatable(ctx.uiKey("pickHint"));
        graphics.text(font, header, width / 2 + 10, 40, 0xFFA0A0A0);
        if (error != null) {
            graphics.textWithWordWrap(font, Component.literal(error), LABEL_X, TOP + type.fields().size() * rowHeight,
                    width / 2 - 30, 0xFFFF5555);
        }
    }

    private final class SuggestionList extends ObjectSelectionList<SuggestionList.Row> {
        private RuleType.@Nullable Field field;

        SuggestionList(Minecraft minecraft, int width, int height, int y) {
            super(minecraft, width, height, y, 20);
        }

        void show(RuleType.@Nullable Field field, List<RuleSuggestionSource.Option> options) {
            this.field = field;
            clearEntries();
            options.forEach(option -> addEntry(new Row(field, option)));
            setScrollAmount(0);
        }

        // Vanilla puts the scrollbar just right of the rows, which here would be outside the widget and unclickable.
        @Override
        protected int scrollBarX() {
            return getRight() - scrollbarWidth();
        }

        @Override
        public int getRowWidth() {
            return width - scrollbarWidth() * 2 - 4;
        }

        final class Row extends ObjectSelectionList.Entry<Row> {
            private final RuleSuggestionSource.Option option;
            private final @Nullable Component help;
            private final String detail;

            Row(RuleType.Field field, RuleSuggestionSource.Option option) {
                this.option = option;
                this.help = ctx.suggestions().help(field, option.id());
                double[] range = field.kind() == RuleType.Kind.ATTRIBUTE ? ctx.suggestions().attributeRange(option.id()) : null;
                this.detail = range == null ? "" : RuleType.format(range[0]) + " - " + RuleType.format(range[1]);
            }

            @Override
            public Component getNarration() {
                return Component.literal(option.name());
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
                int x = getContentX();
                int textY = getContentY() + (getContentHeight() - font.lineHeight) / 2 + 1;
                int right = x + getContentWidth();
                if (!option.icon().isEmpty()) {
                    graphics.item(option.icon(), x, getContentY());
                    x += 20;
                }
                int detailWidth = font.width(detail);
                graphics.text(font, detail, right - detailWidth, textY, 0xFF909090);

                int available = right - x - (detailWidth > 0 ? detailWidth + 6 : 0);
                String name = font.plainSubstrByWidth(option.name(), available);
                graphics.text(font, name, x, textY, hovered ? 0xFFFFFF55 : 0xFFFFFFFF);
                if (!option.id().equals(option.name())) {
                    int idX = x + font.width(name) + 6;
                    String id = font.plainSubstrByWidth(option.id(), Math.max(0, available - (idX - x)));
                    graphics.text(font, id, idX, textY, 0xFF808080);
                }
                if (hovered && help != null) graphics.setTooltipForNextFrame(font.split(help, 200), mouseX, mouseY);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
                pick(option);
                return true;
            }
        }
    }
}
