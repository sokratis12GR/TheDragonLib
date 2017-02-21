package net.thedragonteam.thedragonlib.client.util

import net.minecraft.client.renderer.GlStateManager
import net.thedragonteam.thedragonlib.util.LogHelper
import org.lwjgl.opengl.GL11

object GLStateHelper {

    private var lastBlendState = -1

    fun pushBlend(enable: Boolean) {
        if (lastBlendState != -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to push twice")
        } else {
            lastBlendState = GlStateManager.glGetInteger(GL11.GL_BLEND)
            if (enable) {
                GlStateManager.enableBlend()
            } else {
                GlStateManager.disableBlend()
            }
        }
    }

    fun popBlend() {
        when (lastBlendState) {
            -1 -> LogHelper.bigError("[GLStateHelper] Attempt to pop before pushing")
            else -> {
                when (lastBlendState) {
                    1 -> GlStateManager.enableBlend()
                    else -> GlStateManager.disableBlend()
                }
                lastBlendState = -1
            }
        }
    }

    private var lastAlphaState = -1

    fun pushAlpha(enable: Boolean) {
        if (lastAlphaState != -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to push twice")
        } else {
            lastAlphaState = GlStateManager.glGetInteger(GL11.GL_ALPHA)
            if (enable) {
                GlStateManager.enableAlpha()
            } else {
                GlStateManager.disableAlpha()
            }
        }
    }

    fun popAlpha() {
        if (lastAlphaState == -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to pop before pushing")
        } else {
            if (lastAlphaState == 1) {
                GlStateManager.enableAlpha()
            } else {
                GlStateManager.disableAlpha()
            }
            lastAlphaState = -1
        }
    }

    private var lastDepthState = -1

    fun pushDepth(enable: Boolean) {
        if (lastDepthState != -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to push twice")
        } else {
            lastDepthState = GlStateManager.glGetInteger(GL11.GL_DEPTH)
            if (enable) {
                GlStateManager.enableDepth()
            } else {
                GlStateManager.disableDepth()
            }
        }
    }

    fun popDepth() {
        if (lastDepthState == -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to pop before pushing")
        } else {
            if (lastDepthState == 1) {
                GlStateManager.enableDepth()
            } else {
                GlStateManager.disableDepth()
            }
            lastDepthState = -1
        }
    }
}