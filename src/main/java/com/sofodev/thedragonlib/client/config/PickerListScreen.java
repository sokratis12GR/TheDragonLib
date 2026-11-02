package com.sofodev.thedragonlib.client.config;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * A {@link StringListScreen} that additionally offers an {@link EntryPicker} "pick from a list"
 * button next to the manual add button, for config lists that are really a set of ids (registry
 * entries, mod ids, etc).
 */
public final class PickerListScreen extends StringListScreen {

    private final EntryPicker picker;
    private final Component pickLabel;
    private final Component pickUnavailableTooltip;

    @SuppressWarnings("rawtypes")
    public PickerListScreen(Screen parent, Component title, ForgeConfigSpec.ConfigValue value,
                             EntryPicker picker, Component pickLabel, Component pickUnavailableTooltip) {
        super(parent, title, value);
        this.picker = picker;
        this.pickLabel = pickLabel;
        this.pickUnavailableTooltip = pickUnavailableTooltip;
    }

    @Override
    protected void addBottomButtons() {
        int buttonY = height - 28;

        Button pickButton = Button.builder(pickLabel, b -> picker.open(this, entries, this::addEntries))
                .bounds(width / 2 - 154, buttonY - 48, 150, 20).build();
        if (!picker.isAvailable()) {
            pickButton.active = false;
            pickButton.setTooltip(Tooltip.create(pickUnavailableTooltip));
        }
        addRenderableWidget(pickButton);

        addRenderableWidget(Button.builder(Component.literal("Add"), b -> {
                    entries.add("");
                    rebuildWidgets();
                })
                .bounds(width / 2 + 4, buttonY - 48, 150, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> save())
                .bounds(width / 2 - 154, buttonY, 150, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .bounds(width / 2 + 4, buttonY, 150, 20).build());
    }
}
