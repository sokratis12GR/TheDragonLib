package com.sofodev.thedragonlib.client.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;

/**
 * Asked when leaving a rule screen with changes: Save, Discard or Keep editing (also Esc).
 *
 * @author Sokratis Fotkatzikis
 */
final class UnsavedChangesScreen extends Screen {
    private final Screen editing;
    private final RuleListContext ctx;
    private final Runnable save;
    private final Runnable discard;
    private final @Nullable Component cantSave;

    /** @param cantSave why Save isn't possible right now (an invalid line), null if it is */
    UnsavedChangesScreen(Screen editing, RuleListContext ctx, Runnable save, Runnable discard, @Nullable Component cantSave) {
        super(Component.translatableWithFallback(ctx.uiKey("unsaved"), "Unsaved changes"));
        this.editing = editing;
        this.ctx = ctx;
        this.save = save;
        this.discard = discard;
        this.cantSave = cantSave;
    }

    private Component ui(String suffix, String fallback) {
        return Component.translatableWithFallback(ctx.uiKey(suffix), fallback);
    }

    @Override
    protected void init() {
        int y = height / 2 + 10;
        Button saveButton = addRenderableWidget(Button.builder(ui("unsaved.save", "Save").copy().withStyle(ChatFormatting.GREEN), b -> save.run())
                .bounds(width / 2 - 154, y, 100, 20).build());
        if (cantSave != null) {
            saveButton.active = false;
            saveButton.setTooltip(Tooltip.create(cantSave));
        }
        addRenderableWidget(Button.builder(ui("unsaved.discard", "Discard").copy().withStyle(ChatFormatting.RED), b -> discard.run())
                .bounds(width / 2 - 50, y, 100, 20).build());
        addRenderableWidget(Button.builder(ui("unsaved.keep", "Keep editing"), b -> onClose())
                .bounds(width / 2 + 54, y, 100, 20).build());
    }

    @Override
    public void onClose() {
        minecraft.setScreen(editing);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(font, title, width / 2, height / 2 - 30, 0xFFFFFF55);
        graphics.centeredText(font, ui("unsaved.message", "Save the changes before leaving?"), width / 2, height / 2 - 14, 0xFFFFFFFF);
    }
}
