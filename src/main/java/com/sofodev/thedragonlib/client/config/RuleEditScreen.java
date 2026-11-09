package com.sofodev.thedragonlib.client.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.Nullable;

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
    /** With a note under the input, which only attribute values have. */
    private static final int TALL_ROW = 32;
    private static final int SHORT_ROW = 24;
    private static final int NOTE_OFFSET = 21;
    private static final int GAP = 4;
    /** Between one field's input and the next field's label. */
    private static final int FIELD_GAP = 12;
    private static final int ICON_SIZE = 20;
    /** Keeps the inputs of one and two digit numbers the same size, so the rows below each other line up. */
    private static final int MIN_NUMBER_WIDTH = 26;
    /** The input padding and the cursor. */
    private static final int NUMBER_PADDING = 14;
    private static final int BUTTON_PADDING = 12;
    /** Sized for when a number has no maximum; a longer one still fits, it just scrolls. */
    private static final String LONG_NUMBER = "999999";
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
        // 1.21.1 lists can't size rows individually, so every row gets room for a note when any field has one.
        boolean tall = type.fields().stream().anyMatch(f -> f.kind() == RuleType.Kind.ATTRIBUTE_VALUE);
        rows = addRenderableWidget(new Rows(minecraft, width * 3 / 5 - FORM_X, height - TOP - FOOTER, TOP, tall ? TALL_ROW : SHORT_ROW));
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
        int labelColumn = 0;
        for (List<Integer> group : groups) {
            labelColumn = Math.max(labelColumn, font.width(rowLabel(type.fields().get(group.getFirst()))) + GAP + 2);
        }
        labelColumn = Math.min(labelColumn, contentWidth * 2 / 5);

        for (List<Integer> group : groups) {
            int room = contentWidth;
            int[] labelWidths = new int[group.size()];
            int[] widgetWidths = new int[group.size()];
            int flexible = 0;
            for (int i = 0; i < group.size(); i++) {
                RuleType.Field field = type.fields().get(group.get(i));
                labelWidths[i] = i == 0 ? labelColumn : field.icon() != null ? 0 : font.width(fieldLabel(field)) + GAP;
                widgetWidths[i] = fittedWidth(field);
                room -= labelWidths[i] + widgetWidths[i] + (i == 0 ? 0 : gapBefore(field));
                if (widgetWidths[i] == 0) flexible++;
            }
            List<Rows.Cell> cells = new ArrayList<>();
            for (int i = 0; i < group.size(); i++) {
                int index = group.get(i);
                int widgetWidth = widgetWidths[i] > 0 ? widgetWidths[i] : room / flexible;
                cells.add(new Rows.Cell(index, labelWidths[i], i + 1 < group.size() ? gapBefore(type.fields().get(group.get(i + 1))) : 0, createWidget(index, widgetWidth)));
            }
            Rows.Row row = list.addRow(cells);
            for (int index : group) rowOfField[index] = row;
        }
    }

    private int gapBefore(RuleType.Field field) {
        return field.icon() != null ? GAP : FIELD_GAP;
    }

    /** @return the width that fits everything the input can hold, 0 for pickers and attribute values which share what's left of the row */
    private int fittedWidth(RuleType.Field field) {
        if (field.icon() != null) return ICON_SIZE;
        if (field.isButton()) {
            List<String> labels = new ArrayList<>(field.kind() == RuleType.Kind.TOGGLE ? List.of("true", "false") : field.choices());
            if (field.optional()) labels.add("");
            int widest = 0;
            for (String value : labels) widest = Math.max(widest, font.width(RuleType.buttonLabel(field, value, ctx)));
            return widest + BUTTON_PADDING;
        }
        if (field.kind() != RuleType.Kind.INTEGER && field.kind() != RuleType.Kind.DECIMAL) return 0;
        String decimals = field.kind() == RuleType.Kind.DECIMAL ? ".000" : "";
        int longest = Double.isInfinite(field.max()) ? font.width(LONG_NUMBER)
                : Math.max(font.width(RuleType.format(field.min()) + decimals), font.width(RuleType.format(field.max()) + decimals));
        return Math.max(MIN_NUMBER_WIDTH, longest + NUMBER_PADDING);
    }

    /** @return the field indexes of each row; inline fields join the row of the field before them if that's allowed, and icons always can as they're small */
    private List<List<Integer>> fieldsByRow(boolean allowInline) {
        List<List<Integer>> groups = new ArrayList<>();
        for (int i = 0; i < type.fields().size(); i++) {
            RuleType.Field field = type.fields().get(i);
            if (i > 0 && field.inline() && (allowInline || field.icon() != null)) groups.getLast().add(i);
            else groups.add(new ArrayList<>(List.of(i)));
        }
        return groups;
    }

    private AbstractWidget createWidget(int index, int widgetWidth) {
        RuleType.Field field = type.fields().get(index);
        if (field.icon() != null) {
            boxes[index] = null;
            IconToggle toggle = new IconToggle(index, field.icon());
            toggle.setTooltip(Tooltip.create(tooltip(index)));
            return toggle;
        }
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
        button.setMessage(field.icon() != null ? iconMessage(field, values[index]) : RuleType.buttonLabel(field, values[index], ctx));
        if (index == 0) refreshSuggestions();
        revalidate();
    }

    private Component fieldLabel(RuleType.Field field) {
        return Component.translatable(ctx.uiKey("field." + field.name()));
    }

    /** What the row of a field that comes first in it is labelled. */
    private Component rowLabel(RuleType.Field field) {
        return field.rowLabel() != null ? Component.translatable(ctx.uiKey("field." + field.rowLabel())) : fieldLabel(field);
    }

    /** An icon doesn't show its state as text, so it's only read out by narration. */
    private Component iconMessage(RuleType.Field field, String value) {
        return CommonComponents.optionStatus(fieldLabel(field), value.equals("true"));
    }

    /** Per-rule-type help when one exists for this field, otherwise the shared help for that field. */
    private Component help(RuleType.Field field) {
        String specific = ctx.uiKey("help." + type.key() + "." + field.name());
        return Component.translatable(I18n.exists(specific) ? specific : ctx.uiKey("help." + field.name()));
    }

    /** The help of a field, plus the allowed range for number inputs and the name for icons, as neither shows those itself. */
    private Component tooltip(int index) {
        RuleType.Field field = type.fields().get(index);
        if (field.icon() != null) return Component.empty().append(fieldLabel(field)).append(": ").append(help(field));
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

    /** Small line under an attribute value: its allowed range and the context's default, which depend on the picked attribute. */
    private @Nullable Component note(int index) {
        RuleType.Field field = type.fields().get(index);
        if (field.kind() != RuleType.Kind.ATTRIBUTE_VALUE) return null;
        double[] range = type.range(index, values, ctx.suggestions());
        Double defaultValue = ctx.suggestions().attributeDefault(values[0], type.attributeValue(values));
        if (defaultValue == null) return Component.translatable(ctx.uiKey("note.pickAttribute"));
        Component normal = Component.translatable(ctx.uiKey("note.default"), RuleType.format(defaultValue));
        return range == null ? normal
                : Component.translatable(ctx.uiKey("note.rangeDefault"), rangeText(range, false), normal);
    }

    /** The name of what a picker's ID stands for, at the end of its input; left out if it doesn't fit next to the ID. */
    private void renderPickedName(GuiGraphics graphics, int index, AbstractWidget box, int y) {
        RuleType.Field field = type.fields().get(index);
        String value = values[index];
        if (!field.isPicker() || value.isEmpty() || type.validateField(index, values, ctx) != null) return;
        String name = ctx.suggestions().displayName(field, value);
        int nameWidth = font.width(name);
        if (name.equals(value) || font.width(value) + nameWidth + NUMBER_PADDING + GAP > box.getWidth()) return;
        graphics.drawString(font, name, box.getRight() - 4 - nameWidth, y + 6, 0xFF808080);
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
        super.render(graphics, mouseX, mouseY, a);
        graphics.drawCenteredString(font, title, width / 2, 10, 0xFFFFFFFF);
        RuleListScreen.subtitle(this, graphics, font, Component.translatable(ctx.configKey(type.key() + ".tooltip")), width, 22, mouseX, mouseY);
        Component header = activeField >= 0
                ? Component.translatable(ctx.uiKey("matches"), fieldLabel(type.fields().get(activeField)))
                : Component.translatable(ctx.uiKey("pickHint"));
        if (suggestions != null) graphics.drawString(font, font.plainSubstrByWidth(header.getString(), suggestions.getWidth()), suggestions.getX(), 38, 0xFFA0A0A0);
        renderMessage(graphics, mouseY);
    }

    /** A single line above the buttons: the outcome of Copy or Paste, otherwise what's wrong with the entry. */
    private void renderMessage(GuiGraphics graphics, int mouseY) {
        Component message = notice != null ? notice : error != null ? Component.literal(error).withStyle(ChatFormatting.RED) : null;
        if (message == null) return;
        int y = height - FOOTER + 2;
        int maxWidth = width - FORM_X * 2;
        String full = message.getString();
        graphics.drawString(font, font.plainSubstrByWidth(full, maxWidth), FORM_X, y, message.getStyle().getColor() != null ? 0xFFFF5555 : 0xFF55FF55);
        if (font.width(full) > maxWidth && mouseY >= y && mouseY < y + font.lineHeight) {
            setTooltipForNextRenderPass(font.split(message, 250));
        }
    }

    private final class Rows extends ContainerObjectSelectionList<Rows.Row> {
        Rows(Minecraft minecraft, int width, int height, int y, int rowHeight) {
            super(minecraft, width, height, y, rowHeight);
        }

        Row addRow(List<Cell> cells) {
            Row row = new Row(cells);
            addEntry(row);
            return row;
        }

        // Vanilla puts the scrollbar just right of the rows, which here would be outside the widget and unclickable.
        @Override
        protected int getScrollbarPosition() {
            return getRight() - SCROLLBAR_WIDTH;
        }

        @Override
        public int getRowWidth() {
            return width - SCROLLBAR_WIDTH * 2 - 4;
        }

        /** @param gapAfter the space before the next field of the row */
        record Cell(int field, int labelWidth, int gapAfter, AbstractWidget widget) {
        }

        final class Row extends ContainerObjectSelectionList.Entry<Row> {
            private final List<Cell> cells;
            private final List<AbstractWidget> widgets = new ArrayList<>();

            Row(List<Cell> cells) {
                this.cells = cells;
                for (Cell cell : cells) widgets.add(cell.widget());
            }

            @Override
            public void render(GuiGraphics graphics, int rowIndex, int top, int left, int width, int height,
                               int mouseX, int mouseY, boolean hovered, float a) {
                int y = top + 2;
                int x = left + 2;
                for (Cell cell : cells) {
                    int index = cell.field();
                    RuleType.Field field = type.fields().get(index);
                    // An error names the field by its label, which isn't unique on its own.
                    int color = type.validateField(index, values, ctx) != null ? 0xFFFF5555 : index == activeField ? 0xFFFFFF55 : 0xFFE0E0E0;
                    if (cell.labelWidth() > 0) {
                        Component label = cell == cells.getFirst() ? rowLabel(field) : fieldLabel(field);
                        graphics.drawString(font, font.plainSubstrByWidth(label.getString(), cell.labelWidth() - 2), x, y + 6, color);
                    }
                    // A shared row label says what all of its fields are about, so it isn't this field's help.
                    if (field.rowLabel() == null && hovered && mouseX >= x && mouseX < x + cell.labelWidth() && mouseY < y + 20) {
                        RuleEditScreen.this.setTooltipForNextRenderPass(font.split(tooltip(index), 200));
                    }
                    x += cell.labelWidth();
                    cell.widget().setPosition(x, y);
                    cell.widget().render(graphics, mouseX, mouseY, a);
                    renderPickedName(graphics, index, cell.widget(), y);
                    Component note = note(index);
                    if (note != null && font.width(note) <= cell.widget().getWidth()) {
                        graphics.drawString(font, note, x + 2, y + NOTE_OFFSET, 0xFF909090);
                    }
                    x += cell.widget().getWidth() + cell.gapAfter();
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

    /** The vanilla silhouettes are dark and tinting can't brighten them, so the tile behind one shows the state: light is on. */
    private final class IconToggle extends Button {
        private final int index;
        private final ResourceLocation icon;

        IconToggle(int index, ResourceLocation icon) {
            super(0, 0, ICON_SIZE, ICON_SIZE, iconMessage(type.fields().get(index), values[index]), b -> cycle(index, b), DEFAULT_NARRATION);
            this.index = index;
            this.icon = icon;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float a) {
            graphics.blitSprite(SPRITES.get(active, isHoveredOrFocused()), getX(), getY(), getWidth(), getHeight());
            int x = getX() + 2;
            int y = getY() + 2;
            graphics.fill(x, y, x + 16, y + 16, values[index].equals("true") ? 0xFFD8D8D8 : 0xFF2B2B2B);
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(icon);
            graphics.blit(x, y, 0, 16, 16, sprite);
        }
    }

    private final class SuggestionList extends ObjectSelectionList<SuggestionList.Row> {
        SuggestionList(Minecraft minecraft, int width, int height, int y) {
            super(minecraft, width, height, y, 20);
        }

        void show(RuleType.@Nullable Field field, List<RuleSuggestionSource.Option> options) {
            clearEntries();
            if (field != null) options.forEach(option -> addEntry(new Row(field, option)));
            setScrollAmount(0);
        }

        // Vanilla puts the scrollbar just right of the rows, which here would be outside the widget and unclickable.
        @Override
        protected int getScrollbarPosition() {
            return getRight() - SCROLLBAR_WIDTH;
        }

        @Override
        public int getRowWidth() {
            return width - SCROLLBAR_WIDTH * 2 - 4;
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
            public void render(GuiGraphics graphics, int rowIndex, int top, int left, int width, int height,
                               int mouseX, int mouseY, boolean hovered, float a) {
                int x = left + 2;
                int contentY = top + 2;
                int contentHeight = height - 4;
                int textY = contentY + (contentHeight - font.lineHeight) / 2 + 1;
                int right = left + width - 2;
                if (!option.icon().isEmpty()) {
                    graphics.renderItem(option.icon(), x, contentY);
                    x += 20;
                }
                int detailWidth = font.width(detail);
                graphics.drawString(font, detail, right - detailWidth, textY, 0xFF909090);

                int available = right - x - (detailWidth > 0 ? detailWidth + 6 : 0);
                String name = font.plainSubstrByWidth(option.name(), available);
                graphics.drawString(font, name, x, textY, hovered ? 0xFFFFFF55 : 0xFFFFFFFF);
                if (!option.id().equals(option.name())) {
                    int idX = x + font.width(name) + 6;
                    String id = font.plainSubstrByWidth(option.id(), Math.max(0, available - (idX - x)));
                    graphics.drawString(font, id, idX, textY, 0xFF808080);
                }
                if (hovered && help != null) RuleEditScreen.this.setTooltipForNextRenderPass(font.split(help, 200));
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                pick(option);
                return true;
            }
        }
    }
}
