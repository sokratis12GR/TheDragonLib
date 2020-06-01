/*
 * Copyright (c) TheDragonTeam 2016-2017.
 */

package com.sofodev.thedragonlib.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class GuiUtils extends Screen {
    private Minecraft mc = Minecraft.getInstance();
    private FontRenderer fontRenderer = mc.fontRenderer;

    protected GuiUtils() {
        super(new StringTextComponent(""));
    }

    /**
     * Draws a slot that is disabled...
     *
     * @param x          slot x
     * @param y          slot y
     * @param renderItem Item Render
     */
    public static void drawDisabledSlot(int x, int y, ItemRenderer renderItem) {
        //new GuiUtils().zLevel = 100.f;
        renderItem.zLevel = 100.0f;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int colorOverlay = new Color(139, 139, 139, 200).hashCode();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        GlStateManager.colorMask(true, true, true, false);
        renderItem.renderItemAndEffectIntoGUI(new ItemStack(Blocks.AIR), x, y);
        new GuiUtils().blit(x, y, x + 16, y + 16, colorOverlay, colorOverlay);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

      //  new GuiUtils().zLevel = 0.0f;
        renderItem.zLevel = 0.0f;
    }
}
