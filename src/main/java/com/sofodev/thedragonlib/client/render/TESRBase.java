package com.sofodev.thedragonlib.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TESRBase<T extends TileEntity> extends TileEntityRenderer<T> {
    @Override
    public void renderTileEntityFast(T te, double x, double y, double z, float partialTicks, int destroyStage, BufferBuilder buffer) {
        super.renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, buffer);
    }

    public void renderItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            //RenderHelper.enableStandardItemLighting();
            //GlStateManager.enableLighting();
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        }
    }

    private boolean isLightSet = false;
    private float lastBrightnessX = 0;
    private float lastBrightnessY = 0;

    public void translateScaleTranslate(double translate, double x, double y, double z) {
        GlStateManager.translated(translate, translate, translate);
        GlStateManager.scaled(x, y, z);
        GlStateManager.translated(-translate, -translate, -translate);
    }

    public void translateRotateTranslate(double translate, float angle, float x, float y, float z) {
        GlStateManager.translated(translate, translate, translate);
        GlStateManager.rotatef(angle, x, y, z);
        GlStateManager.translated(-translate, -translate, -translate);
    }

    public void preRenderFancy() {
        GlStateManager.getTexLevelParameter(3553, 10242, 10497);
        GlStateManager.getTexLevelParameter(3553, 10243, 10497);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }

    /**
     * Call before rendering transparent
     */
    public void midRenderFancy() {
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.depthMask(false);
    }

    public void postRenderFancy() {
        GlStateManager.enableTexture();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
    }


//    GL_LINES = 0x1,
//    GL_LINE_LOOP = 0x2,
//    GL_LINE_STRIP = 0x3,
//    GL_TRIANGLES = 0x4,
//    GL_TRIANGLE_STRIP = 0x5,
//    GL_TRIANGLE_FAN = 0x6,
//    GL_QUADS = 0x7,
//    GL_QUAD_STRIP = 0x8,
//    GL_POLYGON = 0x9,
}