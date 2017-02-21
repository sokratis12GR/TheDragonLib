package net.thedragonteam.thedragonlib.client.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.client.config.GuiUtils
import net.thedragonteam.thedragonlib.client.ResourceHelperDL
import org.lwjgl.opengl.GL11

object GuiHelper {
    val PXL128 = 0.0078125
    val PXL256 = 0.00390625

    fun isInRect(x: Int, y: Int, xSize: Int, ySize: Int, mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX <= x + xSize && mouseY >= y && mouseY <= y + ySize
    }

    fun drawTexturedRect(x: Int, y: Int, u: Int, v: Int, width: Int, height: Int) {
        drawTexturedRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble(), u, v, width, height, 0.0, PXL256)
    }

    fun drawTexturedRect(x: Double, y: Double, width: Double, height: Double, u: Int, v: Int, uSize: Int, vSize: Int, zLevel: Double, pxl: Double) {
        val tessellator = Tessellator.getInstance()
        val vertexBuffer = tessellator.buffer
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX)
        vertexBuffer.pos(x, y + height, zLevel).tex(u * pxl, (v + vSize) * pxl).endVertex()
        vertexBuffer.pos(x + width, y + height, zLevel).tex((u + uSize) * pxl, (v + vSize) * pxl).endVertex()
        vertexBuffer.pos(x + width, y, zLevel).tex((u + uSize) * pxl, v * pxl).endVertex()
        vertexBuffer.pos(x, y, zLevel).tex(u * pxl, v * pxl).endVertex()
        tessellator.draw()
    }

    fun drawHoveringText(list: List<*>, x: Int, y: Int, font: FontRenderer, guiWidth: Int, guiHeight: Int) {
        GuiUtils.drawHoveringText(list as MutableList<String>?, x, y, guiWidth, guiHeight, -1, font)
    }

    fun drawHoveringTextScaled(list: List<*>, mouseX: Int, mouseY: Int, font: FontRenderer, fade: Float, scale: Double, guiWidth: Int, guiHeight: Int) {
        var mouseX = mouseX
        var mouseY = mouseY
        if (!list.isEmpty()) {
            GlStateManager.pushMatrix()
            GlStateManager.disableRescaleNormal()
            RenderHelper.disableStandardItemLighting()
            GlStateManager.disableLighting()
            GlStateManager.disableDepth()
            GL11.glScaled(scale, scale, 1.0)
            mouseX = (mouseX / scale).toInt()
            mouseY = (mouseY / scale).toInt()

            var tooltipTextWidth = 0

            for (aList in list) {
                val s = aList as String
                val l = font.getStringWidth(s)

                if (l > tooltipTextWidth) {
                    tooltipTextWidth = l
                }
            }

            var tooltipX = mouseX + 12
            var tooltipY = mouseY - 12
            var tooltipHeight = 6

            if (list.size > 1) {
                tooltipHeight += 2 + (list.size - 1) * 10
            }

            if (tooltipX + tooltipTextWidth > (guiWidth / scale).toInt()) {
                tooltipX -= 28 + tooltipTextWidth
            }

            if (tooltipY + tooltipHeight + 6 > (guiHeight / scale).toInt()) {
                tooltipY = (guiHeight / scale).toInt() - tooltipHeight - 6
            }

            val backgroundColor = -267386864
            drawGradientRect(tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor, fade, scale)
            drawGradientRect(tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor, fade, scale)
            drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor, fade, scale)
            drawGradientRect(tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor, fade, scale)
            drawGradientRect(tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor, fade, scale)
            val k1 = 1347420415
            val l1 = k1 and 16711422 shr 1 or (k1 and -16777216)
            drawGradientRect(tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, k1, l1, fade, scale)
            drawGradientRect(tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, k1, l1, fade, scale)
            drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, k1, k1, fade, scale)
            drawGradientRect(tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, l1, l1, fade, scale)

            var i2 = 0
            while (i2 < list.size) {
                val s1 = list[i2] as String
                GlStateManager.enableBlend()
                GlStateManager.disableAlpha()
                OpenGlHelper.glBlendFunc(770, 771, 1, 0)
                font.drawStringWithShadow(s1, tooltipX.toFloat(), tooltipY.toFloat(), (fade * 240f).toInt() + 0x10 shl 24 or 0x00FFFFFF)
                GlStateManager.enableAlpha()
                tooltipY += 10
                ++i2
            }

            GlStateManager.enableLighting()
            GlStateManager.enableDepth()
            RenderHelper.enableStandardItemLighting()
            GlStateManager.enableRescaleNormal()
            GlStateManager.popMatrix()
        }
    }

    fun drawGradientRect(left: Int, top: Int, right: Int, bottom: Int, colour1: Int, colour2: Int, fade: Float, zLevel: Double) {
        val f = (colour1 shr 24 and 255) / 255.0f * fade
        val f1 = (colour1 shr 16 and 255).toFloat() / 255.0f
        val f2 = (colour1 shr 8 and 255).toFloat() / 255.0f
        val f3 = (colour1 and 255).toFloat() / 255.0f
        val f4 = (colour2 shr 24 and 255) / 255.0f * fade
        val f5 = (colour2 shr 16 and 255).toFloat() / 255.0f
        val f6 = (colour2 shr 8 and 255).toFloat() / 255.0f
        val f7 = (colour2 and 255).toFloat() / 255.0f
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.shadeModel(GL11.GL_SMOOTH)
        val tessellator = Tessellator.getInstance()
        val vertexbuffer = tessellator.buffer
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR)
        vertexbuffer.pos(right.toDouble(), top.toDouble(), zLevel).color(f1, f2, f3, f).endVertex()
        vertexbuffer.pos(left.toDouble(), top.toDouble(), zLevel).color(f1, f2, f3, f).endVertex()
        vertexbuffer.pos(left.toDouble(), bottom.toDouble(), zLevel).color(f5, f6, f7, f4).endVertex()
        vertexbuffer.pos(right.toDouble(), bottom.toDouble(), zLevel).color(f5, f6, f7, f4).endVertex()
        tessellator.draw()
        GlStateManager.shadeModel(GL11.GL_FLAT)
        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()
    }

    fun drawGuiBaseBackground(gui: Gui, posX: Int, posY: Int, xSize: Int, ySize: Int) {
        ResourceHelperDL.bindTexture("textures/gui/baseGui.png")
        GlStateManager.color(1f, 1f, 1f)
        gui.drawTexturedModalRect(posX, posY, 0, 0, xSize - 3, ySize - 3)
        gui.drawTexturedModalRect(posX + xSize - 3, posY, 253, 0, 3, ySize - 3)
        gui.drawTexturedModalRect(posX, posY + ySize - 3, 0, 253, xSize - 3, 3)
        gui.drawTexturedModalRect(posX + xSize - 3, posY + ySize - 3, 253, 253, 3, 3)
    }

    /**
     * Draws the players inventory slots into the gui.
     * note. X-Size is 162
     */
    fun drawPlayerSlots(gui: Gui, posX: Int, posY: Int, center: Boolean) {
        var posX = posX
        ResourceHelperDL.bindTexture("textures/gui/scWidgets.png")

        if (center) {
            posX -= 81
        }

        for (y in 0..2) {
            for (x in 0..8) {
                gui.drawTexturedModalRect(posX + x * 18, posY + y * 18, 138, 0, 18, 18)
            }
        }

        for (x in 0..8) {
            gui.drawTexturedModalRect(posX + x * 18, posY + 58, 138, 0, 18, 18)
        }
    }

    fun drawCenteredString(fontRenderer: FontRenderer, text: String, x: Int, y: Int, color: Int, dropShadow: Boolean) {
        fontRenderer.drawString(text, (x - fontRenderer.getStringWidth(text) / 2).toFloat(), y.toFloat(), color, dropShadow)
    }

    fun drawCenteredSplitString(fontRenderer: FontRenderer, str: String, x: Int, y: Int, wrapWidth: Int, color: Int, dropShadow: Boolean) {
        var y = y
        for (s in fontRenderer.listFormattedStringToWidth(str, wrapWidth)) {
            drawCenteredString(fontRenderer, s, x, y, color, dropShadow)
            y += fontRenderer.FONT_HEIGHT
        }
    }

    fun drawStack2D(stack: ItemStack, mc: Minecraft, x: Int, y: Int, scale: Float) {
        RenderHelper.enableGUIStandardItemLighting()
        GlStateManager.translate(0.0f, 0.0f, 32.0f)
        //this.zLevel = 200.0F;
        mc.renderItem.zLevel = 200.0f
        var font: FontRenderer? = null
        if (!stack.isEmpty) font = stack.item.getFontRenderer(stack)
        if (font == null) font = mc.fontRenderer
        mc.renderItem.renderItemAndEffectIntoGUI(stack, x, y)
        val count = if (stack.count > 1) stack.count.toString() else ""
        mc.renderItem.renderItemOverlayIntoGUI(font!!, stack, x, y, count)
        //this.zLevel = 0.0F;
        mc.renderItem.zLevel = 0.0f
    }

    fun drawStack(stack: ItemStack, mc: Minecraft, x: Int, y: Int, scale: Float) {
        if (stack.isEmpty) {
            return
        }
        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat(), y.toFloat(), 300f)
        GlStateManager.scale(scale, scale, scale)
        GlStateManager.rotate(180f, 1f, 0f, 0f)

        mc.renderItem.renderItem(stack, ItemCameraTransforms.TransformType.NONE)

        GlStateManager.popMatrix()
    }

    fun drawGradientRect(posX: Int, posY: Int, xSize: Int, ySize: Int, colour: Int, colour2: Int) {
        drawGradientRect(posX, posY, posX + xSize, posY + ySize, colour, colour2, 1f, 0.0)
    }

    fun drawColouredRect(posX: Int, posY: Int, xSize: Int, ySize: Int, colour: Int) {
        drawGradientRect(posX, posY, posX + xSize, posY + ySize, colour, colour, 1f, 0.0)
    }

    fun drawBorderedRect(posX: Int, posY: Int, xSize: Int, ySize: Int, borderWidth: Int, fillColour: Int, borderColour: Int) {
        drawColouredRect(posX, posY, xSize, borderWidth, borderColour)
        drawColouredRect(posX, posY + ySize - borderWidth, xSize, borderWidth, borderColour)

        drawColouredRect(posX, posY + borderWidth, borderWidth, ySize - 2 * borderWidth, borderColour)
        drawColouredRect(posX + xSize - borderWidth, posY + borderWidth, borderWidth, ySize - 2 * borderWidth, borderColour)

        drawColouredRect(posX + borderWidth, posY + borderWidth, xSize - 2 * borderWidth, ySize - 2 * borderWidth, fillColour)
    }
}