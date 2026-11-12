package com.sofodev.thedragonlib.client.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.locale.Language;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * A searchable, category-filterable, single- or multi-select list screen for picking entries
 * (by id) to add to a config list. Candidates and categories are supplied by the caller, who also
 * owns every translatable string so this stays free of mod-specific lang keys.
 */
public class EntryPickerScreen extends Screen {

    private static final int ROW_HEIGHT = 24;
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_GRAY = 0xFF909090;
    private static final int COLOR_SELECTED = 0xFF55FF55;
    private static final int COLOR_SELECTED_BG = 0x4055FF55;

    /** @param detail the grey line under the name; the id when null */
    public record Candidate(String id, ItemStack icon, Component name, Object entry, @org.jetbrains.annotations.Nullable Component detail) {
        public Candidate(String id, ItemStack icon, Component name, Object entry) {
            this(id, icon, name, entry, null);
        }

        public Candidate(String id, ItemStack icon, Component name) {
            this(id, icon, name, id);
        }
    }

    public record Category(Component label, Predicate<Candidate> test) {
    }

    /**
     * @param category        label for the category {@link CycleButton}
     * @param search          hint text for the search box
     * @param toggleShown     label for the "select/deselect all shown" button
     * @param confirmMultiple confirm button label for multi-select, given the current selection count
     * @param alreadyAdded    subtext shown under a candidate already present in the target list
     */
    public record Labels(Component category, Component search, Component toggleShown,
                          IntFunction<Component> confirmMultiple, java.util.function.Function<String, Component> alreadyAdded) {
    }

    private final Screen parent;
    private final Set<String> existing;
    private final boolean single;
    private final Consumer<List<String>> onConfirm;
    private final List<Candidate> candidates;
    private final List<Category> categories;
    private final Labels labels;
    private final Set<String> selected = new LinkedHashSet<>();
    private Category category;
    private String query = "";
    private PickerList pickerList;
    private Button confirmButton;
    private @org.jetbrains.annotations.Nullable Component emptyText;

    /**
     * @param categories the category filters; the first entry is used as the default (typically a catch-all)
     * @param existing   ids that are already in the list; shown but not selectable
     * @param single     whether only one entry may be selected at a time
     * @param onConfirm  receives the selected ids, called after returning to {@code parent}
     */
    public EntryPickerScreen(Component title, Screen parent, List<Candidate> candidates, List<Category> categories,
                              Set<String> existing, boolean single, Labels labels, Consumer<List<String>> onConfirm) {
        super(title);
        this.parent = parent;
        this.candidates = candidates;
        this.categories = categories;
        this.category = categories.getFirst();
        this.existing = existing;
        this.single = single;
        this.labels = labels;
        this.onConfirm = onConfirm;
    }

    /** Shown in place of the list when nothing matches, e.g. why there is nothing to pick. */
    public EntryPickerScreen withEmptyText(@org.jetbrains.annotations.Nullable Component text) {
        this.emptyText = text;
        return this;
    }

    /** The text cut to {@code maxWidth} with an ellipsis, or as-is if it fits. */
    private FormattedCharSequence fit(Component text, int maxWidth) {
        if (font.width(text) <= maxWidth) return text.getVisualOrderText();
        FormattedText cut = font.substrByWidth(text, maxWidth - font.width(CommonComponents.ELLIPSIS));
        return Language.getInstance().getVisualOrder(FormattedText.composite(cut, CommonComponents.ELLIPSIS));
    }

    @Override
    protected void init() {
        int cx = width / 2;
        addRenderableWidget(CycleButton.builder(Category::label)
                .withInitialValue(category)
                .withValues(categories)
                .create(cx - 155, 22, 150, 20, labels.category(),
                        (button, value) -> {
                            category = value;
                            refresh();
                        }));
        EditBox search = addRenderableWidget(new EditBox(font, cx + 5, 22, 150, 20, labels.search()));
        search.setHint(labels.search());
        search.setValue(query);
        search.setResponder(value -> {
            query = value;
            refresh();
        });
        pickerList = addRenderableWidget(new PickerList(minecraft, width, height - 50 - 36, 50));
        Button toggleShown = addRenderableWidget(Button.builder(labels.toggleShown(), button -> toggleShown())
                .bounds(cx - 155, height - 28, 100, 20).build());
        toggleShown.active = !single;
        confirmButton = addRenderableWidget(Button.builder(confirmLabel(), button -> confirm())
                .bounds(cx - 50, height - 28, 100, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
                .bounds(cx + 55, height - 28, 100, 20).build());
        refresh();
    }

    private List<Candidate> shown() {
        String q = query.toLowerCase(Locale.ROOT).trim();
        return candidates.stream()
                .filter(category.test())
                .filter(c -> q.isEmpty() || c.id().contains(q) || c.name().getString().toLowerCase(Locale.ROOT).contains(q))
                .toList();
    }

    private void refresh() {
        if (pickerList == null) return;
        pickerList.setEntries(shown());
        pickerList.setScrollAmount(0);
    }

    private void toggleShown() {
        List<String> addable = shown().stream().map(Candidate::id).filter(id -> !existing.contains(id)).toList();
        if (selected.containsAll(addable)) {
            addable.forEach(selected::remove);
        } else {
            selected.addAll(addable);
        }
        updateConfirm();
    }

    private void toggle(Candidate candidate) {
        if (existing.contains(candidate.id())) return;
        if (!selected.remove(candidate.id())) {
            if (single) selected.clear();
            selected.add(candidate.id());
        }
        updateConfirm();
    }

    private Component confirmLabel() {
        return single ? CommonComponents.GUI_DONE : labels.confirmMultiple().apply(selected.size());
    }

    private void updateConfirm() {
        confirmButton.setMessage(confirmLabel());
        confirmButton.active = !selected.isEmpty();
    }

    private void confirm() {
        List<String> result = new java.util.ArrayList<>(selected);
        // return first so the callback can open a follow-up screen
        onClose();
        onConfirm.accept(result);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
        super.render(graphics, mouseX, mouseY, a);
        graphics.drawCenteredString(font, fit(title, width - 20), width / 2, 8, COLOR_WHITE);
        if (font.width(title) > width - 20 && mouseY < 20) setTooltipForNextRenderPass(title);
        if (emptyText != null && pickerList != null && pickerList.children().isEmpty()) {
            List<FormattedCharSequence> lines = font.split(emptyText, Math.min(width - 40, 300));
            int y = 50 + (height - 86) / 2 - lines.size() * 5;
            for (FormattedCharSequence line : lines) {
                graphics.drawCenteredString(font, line, width / 2, y, COLOR_GRAY);
                y += 10;
            }
        }
        if (confirmButton != null) confirmButton.active = !selected.isEmpty();
    }

    private class PickerList extends ObjectSelectionList<PickerEntry> {
        PickerList(net.minecraft.client.Minecraft minecraft, int width, int height, int y) {
            super(minecraft, width, height, y, ROW_HEIGHT);
        }

        void setEntries(List<Candidate> shown) {
            clearEntries();
            shown.forEach(candidate -> addEntry(new PickerEntry(candidate)));
        }

        @Override
        public int getRowWidth() {
            return Math.min(width - 50, 420);
        }
    }

    private class PickerEntry extends ObjectSelectionList.Entry<PickerEntry> {
        private final Candidate candidate;

        PickerEntry(Candidate candidate) {
            this.candidate = candidate;
        }

        @Override
        public Component getNarration() {
            return candidate.name();
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                            int mouseX, int mouseY, boolean hovered, float a) {
            boolean added = existing.contains(candidate.id());
            boolean picked = selected.contains(candidate.id());
            int x = left + 2;
            int y = top + 2;
            int contentWidth = width - 4;
            int contentHeight = height - 4;
            if (picked) {
                graphics.fill(x, y, x + contentWidth, y + contentHeight, COLOR_SELECTED_BG);
            }
            // Two text lines in a block, with the icon centred on it.
            int textTop = y + (contentHeight - 19) / 2;
            if (!candidate.icon().isEmpty()) {
                graphics.renderItem(candidate.icon(), x + 2, textTop + 1);
            }
            int textX = x + 22;
            int textWidth = x + contentWidth - 2 - textX;
            Component sub = added ? labels.alreadyAdded().apply(candidate.id())
                    : candidate.detail() != null ? candidate.detail() : Component.literal(candidate.id());
            graphics.drawString(font, fit(candidate.name(), textWidth), textX, textTop, added ? COLOR_GRAY : picked ? COLOR_SELECTED : COLOR_WHITE);
            graphics.drawString(font, fit(sub, textWidth), textX, textTop + 10, COLOR_GRAY);
            if (hovered && (font.width(candidate.name()) > textWidth || font.width(sub) > textWidth)) {
                setTooltipForNextRenderPass(List.of(candidate.name().getVisualOrderText(), sub.getVisualOrderText()));
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            toggle(candidate);
            return true;
        }
    }
}
