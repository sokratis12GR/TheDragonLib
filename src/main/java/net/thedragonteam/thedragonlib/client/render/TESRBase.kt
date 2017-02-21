package net.thedragonteam.thedragonlib.client.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity

class TESRBase<T : TileEntity> : TileEntitySpecialRenderer<T>() {

    override fun renderTileEntityAt(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {

    }

    fun renderItem(stack: ItemStack) {
        if (!stack.isEmpty) {
            //RenderHelper.enableStandardItemLighting();
            //GlStateManager.enableLighting();
            Minecraft.getMinecraft().renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED)
        }
    }

    private var isLightSet = false
    private var lastBrightnessX = 0f
    private var lastBrightnessY = 0f

    fun setLighting(light: Float) {
        if (!isLightSet) {
            lastBrightnessX = OpenGlHelper.lastBrightnessX
            lastBrightnessY = OpenGlHelper.lastBrightnessY
            isLightSet = true
        }
        GlStateManager.disableLighting()
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light, light)
    }

    fun resetLighting() {
        if (isLightSet) {
            isLightSet = false
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY)
        }
        GlStateManager.enableLighting()
    }

    fun translateScaleTranslate(translate: Double, x: Double, y: Double, z: Double) {
        GlStateManager.translate(translate, translate, translate)
        GlStateManager.scale(x, y, z)
        GlStateManager.translate(-translate, -translate, -translate)
    }

    fun translateRotateTranslate(translate: Double, angle: Float, x: Float, y: Float, z: Float) {
        GlStateManager.translate(translate, translate, translate)
        GlStateManager.rotate(angle, x, y, z)
        GlStateManager.translate(-translate, -translate, -translate)
    }

    fun preRenderFancy() {
        GlStateManager.glTexParameteri(3553, 10242, 10497)
        GlStateManager.glTexParameteri(3553, 10243, 10497)
        GlStateManager.disableCull()
        GlStateManager.disableBlend()
        GlStateManager.depthMask(true)
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
    }

    /**
     * Call before rendering transparent
     */
    fun midRenderFancy() {
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.depthMask(false)
    }

    fun postRenderFancy() {
        GlStateManager.enableTexture2D()
        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()
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