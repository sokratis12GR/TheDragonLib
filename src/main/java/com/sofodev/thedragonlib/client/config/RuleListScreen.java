package com.sofodev.thedragonlib.client.config;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.screens.ConfirmScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Lists the entries of one rule-based config option; changes are only applied on Done. Caller supplies
 * the schema ({@link RuleType}) and the translation keys/id lookups ({@link RuleListContext}).
 *
 * @author Sokratis Fotkatzikis
 */
public final class RuleListScreen extends Screen {
    private static final int LINE_HEIGHT = 11;
    private static final int MIN_ROW_HEIGHT = 28;
    /** Lines up with the Add and Done buttons below the list. */
    private static final int MIN_ROW_WIDTH = 308;
    private static final int MAX_ROW_WIDTH = 560;
    /** Row padding (4), the three buttons with their gaps (158) and the gap before them (8). */
    private static final int BUTTONS_WIDTH = 170;

    private final Screen parent;
    private final RuleType type;
    private final RuleListContext ctx;
    private final List<String> rules;
    private final @Nullable EntryPicker picker;
    private final Consumer<List<String>> onDone;
    private final List<String> original;
    private final Component crumb;
    private @Nullable List<String> defaults;
    private @Nullable RuleConversions conversions;
    /** Feedback from the last copy or paste, shown until {@link #noticeUntil}; kept across rebuilds. */
    private @Nullable Component notice;
    private List<Component> noticeDetail = List.of();
    private long noticeUntil;
    /** The field lines are grouped by, or -1 when grouping is off. */
    private int groupField = -1;
    private int groupThreshold = 2;
    private UnaryOperator<String> groupId = UnaryOperator.identity();
    /** Inside a group: the value new lines start with in {@link #groupField}. */
    private @Nullable String prefill;

    public RuleListScreen(Screen parent, RuleType type, RuleListContext ctx, List<String> rules, Consumer<List<String>> onDone) {
        this(parent, type, ctx, rules, null, onDone);
    }

    /** @param picker adds several entries at once next to the Add button, which then get their details through Edit */
    public RuleListScreen(Screen parent, RuleType type, RuleListContext ctx, List<String> rules, @Nullable EntryPicker picker, Consumer<List<String>> onDone) {
        this(parent, crumbs(parent.getTitle(), Component.translatable(ctx.configKey(type.key()))),
                Component.translatable(ctx.configKey(type.key())), type, ctx, rules, picker, onDone);
    }

    private RuleListScreen(Screen parent, Component title, Component crumb, RuleType type, RuleListContext ctx, List<String> rules,
                           @Nullable EntryPicker picker, Consumer<List<String>> onDone) {
        super(title);
        this.crumb = crumb;
        this.original = List.copyOf(rules);
        this.parent = parent;
        this.type = type;
        this.ctx = ctx;
        this.rules = new ArrayList<>(rules);
        this.picker = picker;
        this.onDone = onDone;
    }

    /** Adds a Reset button that puts these entries back; like every other change it's only applied on Done. */
    public RuleListScreen withDefaults(List<String> defaults) {
        this.defaults = List.copyOf(defaults);
        return this;
    }

    /** Lets lines copied from the mod's other rule lists be pasted here, converted by field name. */
    public RuleListScreen withConversions(RuleConversions conversions) {
        this.conversions = conversions;
        return this;
    }

    /**
     * Collapses lines sharing a value in one field into a single row once there are at least {@code threshold} of them;
     * the row opens a list of just those lines. Only the display changes, the saved lines stay as they are. Invalid lines
     * and wildcard values are never grouped.
     *
     * @param field     index of the field to group by
     * @param threshold how many lines a value needs before they're grouped (at least 2)
     * @param idOf      turns a field value into its group key, e.g. dropping data after an ID
     */
    public RuleListScreen withGrouping(int field, int threshold, UnaryOperator<String> idOf) {
        this.groupField = field;
        this.groupThreshold = Math.max(2, threshold);
        this.groupId = idOf;
        return this;
    }

    public RuleListScreen withGrouping(int field, int threshold) {
        return withGrouping(field, threshold, UnaryOperator.identity());
    }

    /** The group a line belongs to, or null if it isn't grouped. */
    private @Nullable String groupKey(String rule) {
        if (groupField < 0 || type.validate(rule, ctx) != null) return null;
        String[] values = type.split(rule);
        if (groupField >= values.length) return null;
        String value = values[groupField];
        if (value.isEmpty() || value.contains("*")) return null;
        return groupId.apply(value);
    }

    /** Grouped keys mapped to their line indices, in list order; keys below the threshold are left out. */
    private Map<String, List<Integer>> groups() {
        Map<String, List<Integer>> all = new LinkedHashMap<>();
        for (int i = 0; i < rules.size(); i++) {
            String key = groupKey(rules.get(i));
            if (key != null) all.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
        }
        all.values().removeIf(members -> members.size() < groupThreshold);
        return all;
    }

    private String groupName(String key) {
        return ctx.suggestions().displayName(type.fields().get(groupField), key);
    }

    private List<String> groupLines(String key) {
        List<String> lines = new ArrayList<>();
        for (String rule : rules) if (key.equals(groupKey(rule))) lines.add(rule);
        return lines;
    }

    /** Puts a group's edited lines back where the group starts; it's still only saved on this screen's Done. */
    private void replaceGroup(String key, List<String> updated) {
        int at = -1;
        for (int i = rules.size() - 1; i >= 0; i--) {
            if (key.equals(groupKey(rules.get(i)))) {
                rules.remove(i);
                at = i;
            }
        }
        rules.addAll(at < 0 ? rules.size() : at, updated);
    }

    private void openGroup(String key) {
        Component name = Component.literal(groupName(key));
        RuleListScreen screen = new RuleListScreen(this, crumbs(title, name), crumbs(crumb, name), type, ctx, groupLines(key), null,
                updated -> replaceGroup(key, updated));
        screen.conversions = conversions;
        screen.groupField = groupField;
        screen.prefill = key;
        // Grouping stays off inside a group.
        screen.groupThreshold = Integer.MAX_VALUE;
        minecraft.setScreen(screen);
    }

    private void deleteGroup(String key) {
        minecraft.setScreen(new ConfirmScreen(yes -> {
            if (yes) replaceGroup(key, List.of());
            minecraft.setScreen(this);
        }, ui("group.deleteTitle", "Delete every %s entry?", groupName(key)),
                ui("group.deleteMessage", "This removes %s entries. Like other changes, it's only saved on Done.", groupLines(key).size())));
    }

    /** A new line, starting with the group's value when inside one. */
    private String newLine() {
        if (prefill == null) return "";
        String[] values = new String[type.fields().size()];
        Arrays.fill(values, "");
        values[groupField] = prefill;
        return type.join(values);
    }

    /** A breadcrumb title such as "Mod › List › Edit line 3". */
    public static Component crumbs(Component... parts) {
        MutableComponent result = Component.empty();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) result.append(Component.literal(" \u203a ").withStyle(ChatFormatting.DARK_GRAY));
            result.append(i == parts.length - 1 ? parts[i] : parts[i].copy().withStyle(ChatFormatting.GRAY));
        }
        return result;
    }

    private boolean dirty() {
        return !rules.equals(original);
    }

    private void done() {
        onDone.accept(List.copyOf(rules));
        minecraft.setScreen(parent);
    }

    @Override
    protected void init() {
        int rowWidth = rowWidth();
        // 1.21.1 lists can't size rows individually, so every row is as tall as the tallest one needs.
        int textWidth = rowWidth - BUTTONS_WIDTH;
        int rowHeight = MIN_ROW_HEIGHT;
        for (String rule : rules) {
            int lines = font.split(summary(rule), textWidth).size()
                    + (type.validate(rule, ctx) != null ? font.split(Component.literal(rule), textWidth).size() : 0);
            rowHeight = Math.max(rowHeight, lines * LINE_HEIGHT + 6);
        }
        // Ends above the notice and the Copy all row so the last rows and the scrollbar aren't covered.
        RuleRows rows = addRenderableWidget(new RuleRows(minecraft, width, height - 36 - 96, 36, rowHeight, rowWidth));
        Map<String, List<Integer>> groups = groups();
        Set<String> shown = new HashSet<>();
        for (int i = 0; i < rules.size(); i++) {
            String key = groupKey(rules.get(i));
            if (key == null || !groups.containsKey(key)) rows.add(i);
            else if (shown.add(key)) rows.addGroup(key, groups.get(key).size());
        }

        int buttonY = height - 28;
        Button copyAll = addRenderableWidget(Button.builder(copyLabel(ui("copyAll", "Copy all")), b -> copyAll())
                .bounds(width / 2 - 154, buttonY - 48, 150, 20).build());
        copyAll.active = !rules.isEmpty();
        addRenderableWidget(Button.builder(ui("pasteLines", "Paste").copy().withStyle(ChatFormatting.GREEN), b -> paste())
                .tooltip(Tooltip.create(pastePreview()))
                .bounds(width / 2 + 4, buttonY - 48, 150, 20).build());
        addRenderableWidget(Button.builder(Component.translatable(ctx.uiKey("add")),
                        b -> edit(newLine(), -1, line -> rules.add(line)))
                .bounds(width / 2 - 154, buttonY - 24, picker == null ? 308 : 150, 20).build());
        if (picker != null) {
            Button pickButton = addRenderableWidget(Button.builder(Component.translatable(ctx.uiKey("pick")),
                            b -> picker.open(this, List.copyOf(rules), this::addEntries))
                    .bounds(width / 2 + 4, buttonY - 24, 150, 20).build());
            if (!picker.isAvailable()) {
                pickButton.active = false;
                pickButton.setTooltip(Tooltip.create(Component.translatable(ctx.uiKey("pickUnavailable"))));
            }
        }
        int buttonWidth = defaults == null ? 150 : 100;
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> done())
                .bounds(width / 2 - 154, buttonY, buttonWidth, 20).build());
        if (defaults != null) {
            addRenderableWidget(Button.builder(Component.translatable(ctx.uiKey("reset")), b -> {
                        rules.clear();
                        rules.addAll(defaults);
                        rebuildWidgets();
                    })
                    .bounds(width / 2 - 50, buttonY, buttonWidth, 20).build());
        }
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .bounds(width / 2 + 154 - buttonWidth, buttonY, buttonWidth, 20).build());
    }

    /** Only as wide as the longest entry needs, so the buttons stay close to short ones; longer entries wrap at the maximum. */
    private int rowWidth() {
        int widest = 0;
        for (String rule : rules) {
            widest = Math.max(widest, font.width(summary(rule)));
            if (type.validate(rule, ctx) != null) widest = Math.max(widest, font.width(rule));
        }
        return Math.min(Math.max(widest + BUTTONS_WIDTH, MIN_ROW_WIDTH), Math.min(MAX_ROW_WIDTH, width - 40));
    }

    private Component summary(String rule) {
        String error = type.validate(rule, ctx);
        if (error != null) return Component.translatable(ctx.uiKey("invalid"), Component.translatable(error));
        Component description = type.describe(rule, ctx);
        return description != null ? description : Component.literal(rule);
    }

    /** @param index the line being edited, -1 for a new one */
    private void edit(String line, int index, Consumer<String> onSave) {
        Component step = index < 0 ? ui("newLine", "New line") : ui("editLine", "Edit line %s", index + 1);
        minecraft.setScreen(new RuleEditScreen(this, type, ctx, line, crumbs(crumb, step), conversions, onSave));
    }

    static MutableComponent copyLabel(Component label) {
        return Component.literal("\u29c9 ").append(label).withStyle(ChatFormatting.AQUA);
    }

    /** What Paste would add, e.g. "3 lines from Add Spawns", read when the screen is built. */
    private Component pastePreview() {
        RuleClipboard.Parsed parsed = RuleClipboard.parse(ctx, minecraft.keyboardHandler.getClipboard());
        if (parsed.lines().isEmpty()) return ui("pasteLines.empty", "The clipboard has no lines to paste.");
        Component preview = parsed.sourceKey() != null && !parsed.sourceKey().equals(type.key())
                ? ui("pasteLines.from", "%s lines from %s", parsed.lines().size(), Component.translatable(ctx.configKey(parsed.sourceKey())))
                : ui("pasteLines.count", "%s lines", parsed.lines().size());
        return Component.empty().append(preview).append("\n")
                .append(ui("pasteLines.tooltip", "Adds every line on the clipboard; invalid lines and duplicates are skipped.").copy().withStyle(ChatFormatting.GRAY));
    }

    private void notify(Component message, List<Component> detail) {
        notice = message;
        noticeDetail = detail;
        noticeUntil = Util.getMillis() + (detail.isEmpty() ? 4000 : 8000);
    }

    /** A UI key the mod may not have translated yet (added after the mods using this screen shipped). */
    private Component ui(String suffix, String fallback, Object... args) {
        return Component.translatableWithFallback(ctx.uiKey(suffix), fallback, args);
    }

    private void copyAll() {
        minecraft.keyboardHandler.setClipboard(RuleClipboard.write(ctx, type, rules));
        notify(ui("copiedAll", "Copied %s entries", rules.size()).copy().withStyle(ChatFormatting.AQUA), List.of());
        rebuildWidgets();
    }

    private void copy(String rule) {
        minecraft.keyboardHandler.setClipboard(RuleClipboard.write(ctx, type, List.of(rule)));
        notify(ui("copied", "Copied the entry").copy().withStyle(ChatFormatting.AQUA), List.of());
        rebuildWidgets();
    }

    /**
     * Valid lines are added unless they're already in the list. Lines copied from another list are converted first; the
     * skipped ones are listed on hover with the reason.
     */
    private void paste() {
        RuleClipboard.Parsed parsed = RuleClipboard.parse(ctx, minecraft.keyboardHandler.getClipboard());
        if (parsed.lines().isEmpty()) {
            notify(ui("pasteLines.empty", "The clipboard has no lines to paste.").copy().withStyle(ChatFormatting.YELLOW), List.of());
            rebuildWidgets();
            return;
        }
        RuleType from = parsed.sourceKey() != null && !parsed.sourceKey().equals(type.key()) && conversions != null
                ? conversions.type(parsed.sourceKey()) : null;
        int added = 0, duplicates = 0, invalid = 0;
        List<Component> skipped = new ArrayList<>();
        for (String copied : parsed.lines()) {
            String line = from != null ? RuleClipboard.convert(copied, from, type, conversions) : copied;
            String problem = type.validate(line, ctx);
            if (problem != null) {
                invalid++;
                skipped.add(Component.literal(copied).withStyle(ChatFormatting.RED).append(Component.literal(" \u2013 ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.translatable(problem).withStyle(ChatFormatting.GRAY)));
            } else if (rules.contains(line)) {
                duplicates++;
                skipped.add(Component.literal(line).withStyle(ChatFormatting.YELLOW).append(Component.literal(" \u2013 ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(ui("pasteLines.duplicate", "already in the list").copy().withStyle(ChatFormatting.GRAY)));
            } else {
                rules.add(line);
                added++;
            }
        }
        MutableComponent message = ui("pasteLines.added", "Pasted %s", added).copy()
                .withStyle(added > 0 ? ChatFormatting.GREEN : ChatFormatting.GRAY);
        if (duplicates > 0) message.append(Component.literal(" \u00b7 ").withStyle(ChatFormatting.DARK_GRAY))
                .append(ui("pasteLines.duplicates", "%s already in the list", duplicates).copy().withStyle(ChatFormatting.YELLOW));
        if (invalid > 0) message.append(Component.literal(" \u00b7 ").withStyle(ChatFormatting.DARK_GRAY))
                .append(ui("pasteLines.invalid", "%s invalid", invalid).copy().withStyle(ChatFormatting.RED));
        if (from != null) message.append(Component.literal(" \u00b7 ").withStyle(ChatFormatting.DARK_GRAY))
                .append(ui("pasteLines.converted", "converted from %s", Component.translatable(ctx.configKey(from.key()))).copy().withStyle(ChatFormatting.AQUA));
        notify(message, skipped);
        rebuildWidgets();
    }

    private void addEntries(List<String> lines) {
        for (String line : lines) {
            if (!rules.contains(line)) rules.add(line);
        }
        rebuildWidgets();
    }

    /** The breadcrumb title, cut to fit, with a yellow dot while there are unsaved changes. */
    static void title(Screen screen, GuiGraphics graphics, Font font, Component title, boolean dirty, int width, int mouseX, int mouseY) {
        int maxWidth = width - 40;
        Component shown = title;
        if (font.width(title) > maxWidth) {
            shown = Component.literal(font.plainSubstrByWidth(title.getString(), maxWidth - font.width("...")) + "...");
            if (mouseY >= 10 && mouseY < 10 + font.lineHeight) screen.setTooltipForNextRenderPass(title);
        }
        if (dirty) shown = Component.empty().append(shown).append(Component.literal(" \u25cf").withStyle(ChatFormatting.YELLOW));
        graphics.drawCenteredString(font, shown, width / 2, 10, 0xFFFFFFFF);
    }

    /** Explanations can be longer than the screen is wide, so they get cut off and hovering shows them in full. */
    public static void subtitle(Screen screen, GuiGraphics graphics, Font font, Component text, int width, int y, int mouseX, int mouseY) {
        int maxWidth = width - 40;
        String full = text.getString();
        boolean cut = font.width(full) > maxWidth;
        String shown = cut ? font.plainSubstrByWidth(full, maxWidth - font.width("...")) + "..." : full;
        graphics.drawCenteredString(font, Component.literal(shown), width / 2, y, 0xFFA0A0A0);
        if (cut && mouseY >= y && mouseY < y + font.lineHeight) {
            screen.setTooltipForNextRenderPass(font.split(text, 250));
        }
    }

    /** Esc and Cancel ask first when there are changes; Done saves straight away. */
    @Override
    public void onClose() {
        if (!dirty()) {
            minecraft.setScreen(parent);
            return;
        }
        minecraft.setScreen(new UnsavedChangesScreen(this, ctx, this::done, () -> minecraft.setScreen(parent), null));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
        super.render(graphics, mouseX, mouseY, a);
        title(this, graphics, font, title, dirty(), width, mouseX, mouseY);
        subtitle(this, graphics, font, Component.translatable(ctx.configKey(type.key() + ".tooltip")), width, 22, mouseX, mouseY);
        if (rules.isEmpty()) {
            graphics.drawCenteredString(font, Component.translatable(ctx.uiKey("empty")), width / 2, 50, 0xFFA0A0A0);
        }
        if (notice != null && Util.getMillis() < noticeUntil) {
            int y = height - 89;
            graphics.drawCenteredString(font, notice, width / 2, y, 0xFFFFFFFF);
            int half = font.width(notice) / 2;
            if (!noticeDetail.isEmpty() && mouseY >= y - 1 && mouseY < y + font.lineHeight && Math.abs(mouseX - width / 2) <= half) {
                // Hovering keeps it up while reading.
                noticeUntil = Math.max(noticeUntil, Util.getMillis() + 1000);
                List<FormattedCharSequence> lines = new ArrayList<>();
                for (Component line : noticeDetail.subList(0, Math.min(noticeDetail.size(), 15))) lines.addAll(font.split(line, 300));
                if (noticeDetail.size() > 15) lines.add(ui("pasteLines.more", "...and %s more", noticeDetail.size() - 15).getVisualOrderText());
                setTooltipForNextRenderPass(lines);
            }
        }
    }

    private final class RuleRows extends ContainerObjectSelectionList<RuleRows.Entry> {
        private final int rowWidth;

        RuleRows(Minecraft minecraft, int width, int height, int y, int rowHeight, int rowWidth) {
            super(minecraft, width, height, y, rowHeight);
            this.rowWidth = rowWidth;
        }

        void add(int index) {
            addEntry(new Row(index));
        }

        void addGroup(String key, int count) {
            addEntry(new GroupRow(key, count));
        }

        @Override
        public int getRowWidth() {
            return rowWidth;
        }

        abstract class Entry extends ContainerObjectSelectionList.Entry<Entry> {}

        /** Several lines for the same value, shown as one row that opens them. */
        final class GroupRow extends Entry {
            private final List<FormattedCharSequence> labelLines;
            private final Button copyButton;
            private final Button openButton;
            private final Button deleteButton;

            GroupRow(String key, int count) {
                Component label = ui("group.label", "%s – %s entries", Component.literal(groupName(key)).withStyle(ChatFormatting.WHITE), count)
                        .copy().withStyle(ChatFormatting.GOLD);
                this.labelLines = font.split(label, getRowWidth() - BUTTONS_WIDTH);
                this.copyButton = Button.builder(copyLabel(Component.translatable(ctx.uiKey("copy"))), b -> {
                    List<String> lines = groupLines(key);
                    minecraft.keyboardHandler.setClipboard(RuleClipboard.write(ctx, type, lines));
                    RuleListScreen.this.notify(ui("copiedAll", "Copied %s entries", lines.size()).copy().withStyle(ChatFormatting.AQUA), List.of());
                    rebuildWidgets();
                }).size(50, 20).build();
                this.openButton = Button.builder(ui("group.open", "Open"), b -> openGroup(key)).size(50, 20).build();
                this.deleteButton = Button.builder(Component.translatable(ctx.uiKey("delete")).copy().withStyle(ChatFormatting.RED),
                        b -> deleteGroup(key)).size(50, 20).build();
            }

            @Override
            public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                               int mouseX, int mouseY, boolean hovered, float a) {
                int contentY = top + 2;
                int contentRight = left + width - 2;
                int contentHeight = height - 4;
                int buttonY = contentY + (contentHeight - 20) / 2;
                deleteButton.setPosition(contentRight - deleteButton.getWidth(), buttonY);
                openButton.setPosition(deleteButton.getX() - 4 - openButton.getWidth(), buttonY);
                copyButton.setPosition(openButton.getX() - 4 - copyButton.getWidth(), buttonY);
                copyButton.render(graphics, mouseX, mouseY, a);
                openButton.render(graphics, mouseX, mouseY, a);
                deleteButton.render(graphics, mouseX, mouseY, a);
                int textY = contentY + (contentHeight - labelLines.size() * LINE_HEIGHT) / 2 + 1;
                for (FormattedCharSequence line : labelLines) {
                    graphics.drawString(font, line, left + 2, textY, 0xFFFFFFFF);
                    textY += LINE_HEIGHT;
                }
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of(copyButton, openButton, deleteButton);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of(copyButton, openButton, deleteButton);
            }
        }

        final class Row extends Entry {
            private final @Nullable String error;
            private final List<FormattedCharSequence> summaryLines;
            /** The line as saved: shown when it's invalid, otherwise on hover if the summary replaces it. */
            private final List<FormattedCharSequence> rawLines;
            private final List<FormattedCharSequence> rawTooltip;
            private final Button copyButton;
            private final Button editButton;
            private final Button deleteButton;

            Row(int index) {
                this.error = type.validate(rules.get(index), ctx);
                boolean described = type.describe(rules.get(index), ctx) != null;
                int textWidth = getRowWidth() - BUTTONS_WIDTH;
                this.summaryLines = font.split(summary(rules.get(index)), textWidth);
                this.rawLines = error != null ? font.split(Component.literal(rules.get(index)), textWidth) : List.of();
                this.rawTooltip = error == null && described ? font.split(Component.literal(rules.get(index)), 250) : List.of();
                this.copyButton = Button.builder(copyLabel(Component.translatable(ctx.uiKey("copy"))),
                        b -> copy(rules.get(index))).size(50, 20)
                        .tooltip(Tooltip.create(Component.literal(rules.get(index)).withStyle(ChatFormatting.GRAY))).build();
                this.editButton = Button.builder(Component.translatable(ctx.uiKey("edit")),
                        b -> edit(rules.get(index), index, line -> rules.set(index, line))).size(50, 20).build();
                this.deleteButton = Button.builder(Component.translatable(ctx.uiKey("delete")).copy().withStyle(ChatFormatting.RED), b -> {
                    rules.remove(index);
                    rebuildWidgets();
                }).size(50, 20).build();
                if (error != null) {
                    editButton.setTooltip(Tooltip.create(Component.translatable(ctx.uiKey("invalid"),
                            Component.translatable(error))));
                }
            }

            @Override
            public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                               int mouseX, int mouseY, boolean hovered, float a) {
                int contentX = left + 2;
                int contentY = top + 2;
                int contentRight = left + width - 2;
                int contentHeight = height - 4;
                int buttonY = contentY + (contentHeight - 20) / 2;
                deleteButton.setPosition(contentRight - deleteButton.getWidth(), buttonY);
                editButton.setPosition(deleteButton.getX() - 4 - editButton.getWidth(), buttonY);
                copyButton.setPosition(editButton.getX() - 4 - copyButton.getWidth(), buttonY);
                copyButton.render(graphics, mouseX, mouseY, a);
                editButton.render(graphics, mouseX, mouseY, a);
                deleteButton.render(graphics, mouseX, mouseY, a);
                int textY = contentY + (contentHeight - (summaryLines.size() + rawLines.size()) * LINE_HEIGHT) / 2 + 1;
                for (FormattedCharSequence line : summaryLines) {
                    graphics.drawString(font, line, contentX, textY, error == null ? 0xFFFFFFFF : 0xFFFF5555);
                    textY += LINE_HEIGHT;
                }
                for (FormattedCharSequence line : rawLines) {
                    graphics.drawString(font, line, contentX, textY, 0xFF808080);
                    textY += LINE_HEIGHT;
                }
                if (hovered && mouseX < copyButton.getX() && !rawTooltip.isEmpty()) {
                    setTooltipForNextRenderPass(rawTooltip);
                }
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of(copyButton, editButton, deleteButton);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of(copyButton, editButton, deleteButton);
            }
        }
    }
}
