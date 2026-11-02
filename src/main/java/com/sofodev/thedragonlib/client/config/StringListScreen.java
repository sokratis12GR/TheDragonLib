package com.sofodev.thedragonlib.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic add/edit/remove editor for a {@code List}-typed {@link ForgeConfigSpec.ConfigValue}. Edits
 * are only written back to the config on Done. {@link PickerListScreen} extends this to additionally
 * offer an {@link EntryPicker} for adding entries.
 */
public class StringListScreen extends Screen {

    protected final Screen parent;
    @SuppressWarnings("rawtypes")
    private final ForgeConfigSpec.ConfigValue value;
    protected final List<String> entries;
    protected Rows rows;

    @SuppressWarnings("rawtypes")
    public StringListScreen(Screen parent, Component title, ForgeConfigSpec.ConfigValue value) {
        super(title);
        this.parent = parent;
        this.value = value;
        this.entries = new ArrayList<>();
        for (Object o : (List<?>) value.get()) {
            entries.add(String.valueOf(o));
        }
    }

    @Override
    protected void init() {
        rows = addRenderableWidget(new Rows(minecraft, width, height - 36 - 60, 36));
        for (int i = 0; i < entries.size(); i++) {
            rows.add(i);
        }
        addBottomButtons();
    }

    protected void addBottomButtons() {
        int buttonY = height - 28;
        addRenderableWidget(Button.builder(Component.literal("Add"), b -> {
                    entries.add("");
                    rebuildWidgets();
                })
                .bounds(width / 2 - 154, buttonY - 24, 308, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> save())
                .bounds(width / 2 - 154, buttonY, 150, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .bounds(width / 2 + 4, buttonY, 150, 20).build());
    }

    protected void addEntries(List<String> ids) {
        boolean changed = false;
        for (String id : ids) {
            if (!entries.contains(id)) {
                entries.add(id);
                changed = true;
            }
        }
        if (changed) {
            rebuildWidgets();
        }
    }

    @SuppressWarnings("unchecked")
    protected void save() {
        value.set(List.copyOf(entries));
        onClose();
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(font, title, width / 2, 14, 0xFFFFFFFF);
    }

    protected final class Rows extends ContainerObjectSelectionList<Rows.Row> {

        Rows(Minecraft minecraft, int width, int height, int y) {
            super(minecraft, width, height, y, 24);
        }

        void add(int index) {
            addEntry(new Row(index));
        }

        @Override
        public int getRowWidth() {
            return Math.min(360, width - 40);
        }

        final class Row extends ContainerObjectSelectionList.Entry<Row> {

            private final EditBox box;
            private final Button delete;

            Row(int index) {
                int boxWidth = Rows.this.getRowWidth() - 54;
                box = new EditBox(font, 0, 0, boxWidth, 20, Component.empty());
                box.setMaxLength(256);
                box.setValue(entries.get(index));
                box.setResponder(text -> entries.set(index, text));
                delete = Button.builder(Component.literal("X"), b -> {
                    entries.remove(index);
                    rebuildWidgets();
                }).size(50, 20).build();
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
                int y = getContentY() + (getContentHeight() - 20) / 2;
                box.setPosition(getContentX(), y);
                delete.setPosition(getContentRight() - delete.getWidth(), y);
                box.extractRenderState(graphics, mouseX, mouseY, a);
                delete.extractRenderState(graphics, mouseX, mouseY, a);
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of(box, delete);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of(box, delete);
            }
        }
    }
}
