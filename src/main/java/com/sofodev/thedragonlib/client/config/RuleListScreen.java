package com.sofodev.thedragonlib.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Lists the entries of one rule-based config option; changes are only applied on Done. Caller supplies
 * the schema ({@link RuleType}) and the translation keys/id lookups ({@link RuleListContext}).
 *
 * @author Sokratis Fotkatzikis
 */
public final class RuleListScreen extends Screen {
    private static final int LINE_HEIGHT = 11;

    private final Screen parent;
    private final RuleType type;
    private final RuleListContext ctx;
    private final List<String> rules;
    private final @Nullable EntryPicker picker;
    private final Consumer<List<String>> onDone;
    private @Nullable List<String> defaults;

    public RuleListScreen(Screen parent, RuleType type, RuleListContext ctx, List<String> rules, Consumer<List<String>> onDone) {
        this(parent, type, ctx, rules, null, onDone);
    }

    /** @param picker adds several entries at once next to the Add button, which then get their details through Edit */
    public RuleListScreen(Screen parent, RuleType type, RuleListContext ctx, List<String> rules, @Nullable EntryPicker picker, Consumer<List<String>> onDone) {
        super(Component.translatable(ctx.configKey(type.key())));
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

    @Override
    protected void init() {
        // Ends above the Add button so the last rows and the scrollbar aren't covered.
        RuleRows rows = addRenderableWidget(new RuleRows(minecraft, width, height - 36 - 60, 36));
        for (int i = 0; i < rules.size(); i++) rows.add(i);

        int buttonY = height - 28;
        addRenderableWidget(Button.builder(Component.translatable(ctx.uiKey("add")),
                        b -> edit("", line -> rules.add(line)))
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
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> {
                    onDone.accept(List.copyOf(rules));
                    minecraft.setScreen(parent);
                })
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

    private void edit(String line, Consumer<String> onSave) {
        minecraft.setScreen(new RuleEditScreen(this, type, ctx, line, onSave));
    }

    private void addEntries(List<String> lines) {
        for (String line : lines) {
            if (!rules.contains(line)) rules.add(line);
        }
        rebuildWidgets();
    }

    /** Explanations can be longer than the screen is wide, so they get cut off and hovering shows them in full. */
    public static void subtitle(GuiGraphicsExtractor graphics, Font font, Component text, int width, int y, int mouseX, int mouseY) {
        int maxWidth = width - 40;
        String full = text.getString();
        boolean cut = font.width(full) > maxWidth;
        String shown = cut ? font.plainSubstrByWidth(full, maxWidth - font.width("...")) + "..." : full;
        graphics.centeredText(font, Component.literal(shown), width / 2, y, 0xFFA0A0A0);
        if (cut && mouseY >= y && mouseY < y + font.lineHeight) {
            graphics.setTooltipForNextFrame(font.split(text, 250), mouseX, mouseY);
        }
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(font, title, width / 2, 10, 0xFFFFFFFF);
        subtitle(graphics, font, Component.translatable(ctx.configKey(type.key() + ".tooltip")), width, 22, mouseX, mouseY);
        if (rules.isEmpty()) {
            graphics.centeredText(font, Component.translatable(ctx.uiKey("empty")), width / 2, 50, 0xFFA0A0A0);
        }
    }

    private final class RuleRows extends ContainerObjectSelectionList<RuleRows.Row> {
        RuleRows(Minecraft minecraft, int width, int height, int y) {
            super(minecraft, width, height, y, 28);
        }

        void add(int index) {
            Row row = new Row(index);
            addEntry(row, row.height());
        }

        @Override
        public int getRowWidth() {
            return Math.min(560, width - 40);
        }

        final class Row extends ContainerObjectSelectionList.Entry<Row> {
            private final @Nullable String error;
            private final List<FormattedCharSequence> summaryLines;
            /** The line as saved: shown when it's invalid, otherwise on hover if the summary replaces it. */
            private final List<FormattedCharSequence> rawLines;
            private final List<FormattedCharSequence> rawTooltip;
            private final Button editButton;
            private final Button deleteButton;

            Row(int index) {
                this.error = type.validate(rules.get(index), ctx);
                Component description = type.describe(rules.get(index), ctx);
                Component summary = error != null
                        ? Component.translatable(ctx.uiKey("invalid"), Component.translatable(error))
                        : description != null ? description : Component.literal(rules.get(index));
                // Row padding (4), both buttons with their gap (104) and the gap before them (8).
                int textWidth = getRowWidth() - 116;
                this.summaryLines = font.split(summary, textWidth);
                this.rawLines = error != null ? font.split(Component.literal(rules.get(index)), textWidth) : List.of();
                this.rawTooltip = error == null && description != null ? font.split(Component.literal(rules.get(index)), 250) : List.of();
                this.editButton = Button.builder(Component.translatable(ctx.uiKey("edit")),
                        b -> edit(rules.get(index), line -> rules.set(index, line))).size(50, 20).build();
                this.deleteButton = Button.builder(Component.translatable(ctx.uiKey("delete")), b -> {
                    rules.remove(index);
                    rebuildWidgets();
                }).size(50, 20).build();
                if (error != null) {
                    editButton.setTooltip(Tooltip.create(Component.translatable(ctx.uiKey("invalid"),
                            Component.translatable(error))));
                }
            }

            /** Tall enough for every wrapped line, and never shorter than the buttons need. */
            int height() {
                return Math.max(28, (summaryLines.size() + rawLines.size()) * LINE_HEIGHT + 6);
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
                int buttonY = getContentY() + (getContentHeight() - 20) / 2;
                deleteButton.setPosition(getContentRight() - deleteButton.getWidth(), buttonY);
                editButton.setPosition(deleteButton.getX() - 4 - editButton.getWidth(), buttonY);
                editButton.extractRenderState(graphics, mouseX, mouseY, a);
                deleteButton.extractRenderState(graphics, mouseX, mouseY, a);
                int textY = getContentY() + 2;
                for (FormattedCharSequence line : summaryLines) {
                    graphics.text(font, line, getContentX(), textY, error == null ? 0xFFFFFFFF : 0xFFFF5555);
                    textY += LINE_HEIGHT;
                }
                for (FormattedCharSequence line : rawLines) {
                    graphics.text(font, line, getContentX(), textY, 0xFF808080);
                    textY += LINE_HEIGHT;
                }
                if (hovered && mouseX < editButton.getX() && !rawTooltip.isEmpty()) {
                    graphics.setTooltipForNextFrame(rawTooltip, mouseX, mouseY);
                }
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of(editButton, deleteButton);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of(editButton, deleteButton);
            }
        }
    }
}
