package com.sofodev.thedragonlib.client.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
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
 * Edits a single rule line: a scrolling form with one input per field, a searchable list of valid IDs, and buttons to copy
 * the line as text and paste it back.
 *
 * @author Sokratis Fotkatzikis
 */
final class RuleEditScreen extends Screen {
    private static final int FORM_X = 10;
    private static final int TOP = 36;
    private static final int FOOTER = 40;
    /** With a note under the inputs, used while every row fits. */
    private static final int TALL_ROW = 32;
    private static final int SHORT_ROW = 24;
    private static final int NOTE_OFFSET = 21;
    private static final int GAP = 4;
    private static final int NUMBER_WIDTH = 40;
    /** Below this every field gets its own row, even the inline ones. */
    private static final int MIN_INLINE_WIDTH = 240;

    private final Screen parent;
    private final RuleType type;
    private final RuleListContext ctx;
    private final String[] values;
    private final Consumer<String> onSave;
    /** One per field, null for the fields that use a button. */
    private final EditBox[] boxes;
    private final Rows.Row[] rowOfField;
    private int activeField = -1;
    /** The box to focus next tick, since a click on the suggestion list gives that list the focus after picking. */
    private int pendingFocus = -1;
    private boolean showNotes;
    private @Nullable Rows rows;
    private @Nullable SuggestionList suggestions;
    private @Nullable Button saveButton;
    private @Nullable Button copyButton;
    private @Nullable String error;
    private @Nullable Component notice;

    RuleEditScreen(Screen parent, RuleType type, RuleListContext ctx, String line, Consumer<String> onSave) {
        super(Component.translatable(ctx.uiKey("editTitle"), Component.translatable(ctx.configKey(type.key()))));
        this.parent = parent;
        this.type = type;
        this.ctx = ctx;
        this.values = withButtonDefaults(type.split(line));
        this.boxes = new EditBox[values.length];
        this.rowOfField = new Rows.Row[values.length];
        this.onSave = onSave;
    }

    private String[] withButtonDefaults(String[] loaded) {
        for (int i = 0; i < loaded.length; i++) {
            RuleType.Field field = type.fields().get(i);
            if (loaded[i].isEmpty() && field.kind() == RuleType.Kind.CHOICE && !field.optional()) loaded[i] = field.choices().getFirst();
            if (loaded[i].isEmpty() && field.kind() == RuleType.Kind.TOGGLE) loaded[i] = "false";
        }
        return loaded;
    }

    @Override
    protected void init() {
        rows = addRenderableWidget(new Rows(minecraft, width * 3 / 5 - FORM_X, height - TOP - FOOTER, TOP));
        rows.setX(FORM_X);
        buildRows(rows);

        int listX = rows.getRight() + 6;
        suggestions = addRenderableWidget(new SuggestionList(minecraft, width - listX - FORM_X, height - 50 - FOOTER, 50));
        suggestions.setX(listX);

        int buttonWidth = Math.min(100, (width - 32) / 4);
        int buttonX = (width - buttonWidth * 4 - 12) / 2;
        copyButton = addRenderableWidget(Button.builder(Component.translatable(ctx.uiKey("copy")), b -> copy())
                .bounds(buttonX, height - 28, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(ctx.uiKey("paste")), b -> paste())
                .bounds(buttonX + buttonWidth + 4, height - 28, buttonWidth, 20).build());
        saveButton = addRenderableWidget(Button.builder(Component.translatable(ctx.uiKey("save")), b -> save())
                .bounds(buttonX + (buttonWidth + 4) * 2, height - 28, buttonWidth, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .bounds(buttonX + (buttonWidth + 4) * 3, height - 28, buttonWidth, 20).build());

        int first = activeField >= 0 ? activeField : nextBox(0);
        activeField = -1;
        if (first >= 0) {
            setActiveField(type.fields().get(first).isPicker() ? first : -1);
            focusBox(first);
        }
        revalidate();
    }

    private void buildRows(Rows list) {
        int contentWidth = list.getRowWidth() - 4;
        List<List<Integer>> groups = fieldsByRow(contentWidth >= MIN_INLINE_WIDTH);
        // The list adds 4 to its content height.
        showNotes = groups.size() * TALL_ROW + 4 <= list.getHeight();
        int labelColumn = 0;
        for (List<Integer> group : groups) {
            labelColumn = Math.max(labelColumn, font.width(fieldLabel(type.fields().get(group.getFirst()))) + GAP + 2);
        }
        labelColumn = Math.min(labelColumn, contentWidth * 2 / 5);

        for (List<Integer> group : groups) {
            int room = contentWidth - GAP * (group.size() - 1);
            int[] labelWidths = new int[group.size()];
            int pickers = 0;
            for (int i = 0; i < group.size(); i++) {
                RuleType.Field field = type.fields().get(group.get(i));
                labelWidths[i] = i == 0 ? labelColumn : font.width(fieldLabel(field)) + GAP;
                room -= labelWidths[i];
                if (field.isPicker()) pickers++;
            }
            // Pickers need the room for their long IDs, otherwise all inputs get the same size.
            int fixed = pickers > 0 ? (group.size() - pickers) * NUMBER_WIDTH : 0;
            int share = (room - fixed) / (pickers > 0 ? pickers : group.size());
            List<Rows.Cell> cells = new ArrayList<>();
            for (int i = 0; i < group.size(); i++) {
                int index = group.get(i);
                int widgetWidth = pickers > 0 && !type.fields().get(index).isPicker() ? NUMBER_WIDTH : share;
                cells.add(new Rows.Cell(index, labelWidths[i], createWidget(index, widgetWidth)));
            }
            Rows.Row row = list.addRow(cells, showNotes ? TALL_ROW : SHORT_ROW);
            for (int index : group) rowOfField[index] = row;
        }
    }

    /** @return the field indexes of each row; inline fields join the row of the field before them if that's allowed */
    private List<List<Integer>> fieldsByRow(boolean allowInline) {
        List<List<Integer>> groups = new ArrayList<>();
        for (int i = 0; i < type.fields().size(); i++) {
            if (allowInline && i > 0 && type.fields().get(i).inline()) groups.getLast().add(i);
            else groups.add(new ArrayList<>(List.of(i)));
        }
        return groups;
    }

    private AbstractWidget createWidget(int index, int widgetWidth) {
        RuleType.Field field = type.fields().get(index);
        if (field.isButton()) {
            boxes[index] = null;
            return Button.builder(RuleType.buttonLabel(field, values[index], ctx), b -> cycle(index, b))
                    .size(widgetWidth, 20)
                    .tooltip(Tooltip.create(tooltip(index))).build();
        }
        EditBox box = new EditBox(font, 0, 0, widgetWidth, 20, fieldLabel(field));
        box.setMaxLength(256);
        box.setValue(values[index]);
        box.setHint(hint(field, widgetWidth));
        box.setTooltip(Tooltip.create(tooltip(index)));
        box.setResponder(value -> {
            values[index] = value.trim();
            // What a picker offers can depend on the line's first field, so edits there refresh it too.
            if (index == activeField || index == 0) refreshSuggestions();
            revalidate();
        });
        boxes[index] = box;
        return box;
    }

    /** Focuses the box in its row, scrolling to it if needed. */
    private void focusBox(int index) {
        if (rows != null) changeFocus(ComponentPath.path(boxes[index], rowOfField[index], rows, this));
    }

    private int nextBox(int from) {
        for (int i = from; i < boxes.length; i++) {
            if (boxes[i] != null) return i;
        }
        return -1;
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

    /** The help of a field, plus the allowed range for number inputs, which are often too small to show it as a note. */
    private Component tooltip(int index) {
        RuleType.Field field = type.fields().get(index);
        if (field.kind() != RuleType.Kind.INTEGER && field.kind() != RuleType.Kind.DECIMAL) return help(field);
        return Component.empty().append(help(field)).append("\n").append(rangeText(new double[] {field.min(), field.max()}, field.percent()));
    }

    /** What an input shows while empty, left out if it wouldn't fit in the input. */
    private Component hint(RuleType.Field field, int boxWidth) {
        Component hint = field.isPicker() && field.wildcard() ? Component.translatable(ctx.uiKey("hint.wildcard"))
                : field.fallback() != null ? Component.literal(field.fallback())
                : field.optional() ? Component.translatable(ctx.uiKey("hint.optional"))
                : Component.empty();
        return font.width(hint) > boxWidth - 8 ? Component.empty() : hint.copy().withStyle(ChatFormatting.DARK_GRAY);
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
        boxes[activeField].setValue(option.id());
        int next = nextBox(activeField + 1);
        pendingFocus = next >= 0 ? next : activeField;
        if (next >= 0 && type.fields().get(next).isPicker()) setActiveField(next);
    }

    private void revalidate() {
        error = null;
        notice = null;
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
        if (copyButton != null) copyButton.active = error == null;
    }

    private void copy() {
        if (error != null) return;
        minecraft.keyboardHandler.setClipboard(type.join(values));
        notice = Component.translatable(ctx.uiKey("copied"));
    }

    private void paste() {
        String line = minecraft.keyboardHandler.getClipboard().trim();
        String problem = type.validate(line, ctx);
        if (problem != null) {
            notice = Component.translatable(ctx.uiKey("invalid"), Component.translatable(problem)).withStyle(ChatFormatting.RED);
            return;
        }
        String[] pasted = withButtonDefaults(type.split(line));
        System.arraycopy(pasted, 0, values, 0, values.length);
        rebuildWidgets();
        notice = Component.translatable(ctx.uiKey("pasted"));
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
        if (pendingFocus >= 0) {
            focusBox(pendingFocus);
            pendingFocus = -1;
        }
        // The suggestion list takes focus when clicked, so only switch fields when another box is focused.
        if (rows != null && getFocused() == rows && rows.getFocused() != null && rows.getFocused().getFocused() instanceof EditBox focused) {
            int index = Arrays.asList(boxes).indexOf(focused);
            if (index >= 0 && type.fields().get(index).isPicker()) setActiveField(index);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(font, title, width / 2, 10, 0xFFFFFFFF);
        RuleListScreen.subtitle(graphics, font, Component.translatable(ctx.configKey(type.key() + ".tooltip")), width, 22, mouseX, mouseY);
        Component header = activeField >= 0
                ? Component.translatable(ctx.uiKey("matches"), fieldLabel(type.fields().get(activeField)))
                : Component.translatable(ctx.uiKey("pickHint"));
        if (suggestions != null) graphics.text(font, font.plainSubstrByWidth(header.getString(), suggestions.getWidth()), suggestions.getX(), 38, 0xFFA0A0A0);
        extractMessage(graphics, mouseX, mouseY);
    }

    /** A single line above the buttons: the outcome of Copy or Paste, otherwise what's wrong with the entry. */
    private void extractMessage(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Component message = notice != null ? notice : error != null ? Component.literal(error).withStyle(ChatFormatting.RED) : null;
        if (message == null) return;
        int y = height - FOOTER + 2;
        int maxWidth = width - FORM_X * 2;
        String full = message.getString();
        graphics.text(font, font.plainSubstrByWidth(full, maxWidth), FORM_X, y, message.getStyle().getColor() != null ? 0xFFFF5555 : 0xFF55FF55);
        if (font.width(full) > maxWidth && mouseY >= y && mouseY < y + font.lineHeight) {
            graphics.setTooltipForNextFrame(font.split(message, 250), mouseX, mouseY);
        }
    }

    private final class Rows extends ContainerObjectSelectionList<Rows.Row> {
        Rows(Minecraft minecraft, int width, int height, int y) {
            super(minecraft, width, height, y, SHORT_ROW);
        }

        Row addRow(List<Cell> cells, int rowHeight) {
            Row row = new Row(cells);
            addEntry(row, rowHeight);
            return row;
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

        record Cell(int field, int labelWidth, AbstractWidget widget) {
        }

        final class Row extends ContainerObjectSelectionList.Entry<Row> {
            private final List<Cell> cells;
            private final List<AbstractWidget> widgets = new ArrayList<>();

            Row(List<Cell> cells) {
                this.cells = cells;
                for (Cell cell : cells) widgets.add(cell.widget());
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
                int y = getContentY();
                int x = getContentX();
                for (Cell cell : cells) {
                    int index = cell.field();
                    RuleType.Field field = type.fields().get(index);
                    // An error names the field by its label, which isn't unique on its own.
                    int color = type.validateField(index, values, ctx) != null ? 0xFFFF5555 : index == activeField ? 0xFFFFFF55 : 0xFFE0E0E0;
                    graphics.text(font, font.plainSubstrByWidth(fieldLabel(field).getString(), cell.labelWidth() - 2), x, y + 6, color);
                    if (hovered && mouseX >= x && mouseX < x + cell.labelWidth() && mouseY < y + 20) {
                        graphics.setTooltipForNextFrame(font.split(tooltip(index), 200), mouseX, mouseY);
                    }
                    x += cell.labelWidth();
                    cell.widget().setPosition(x, y);
                    cell.widget().extractRenderState(graphics, mouseX, mouseY, a);
                    Component note = showNotes ? note(index) : null;
                    if (note != null) {
                        String text = note.getString();
                        int room = cell.widget().getWidth();
                        // A cut off name still helps, a cut off range doesn't and the tooltip has it anyway.
                        if (field.isPicker() || font.width(text) <= room) {
                            graphics.text(font, font.plainSubstrByWidth(text, room), x + 2, y + NOTE_OFFSET, 0xFF909090);
                        }
                    }
                    x += cell.widget().getWidth() + GAP;
                }
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return widgets;
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return widgets;
            }
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
