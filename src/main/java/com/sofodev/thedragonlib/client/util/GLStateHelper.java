package com.sofodev.thedragonlib.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.sofodev.thedragonlib.util.LogHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class GLStateHelper {

    private static int lastBlendState = -1;

    public static void pushBlend(boolean enable) {
        if (lastBlendState != -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to push twice");
        } else {
            lastBlendState = GlStateManager._getInteger(GL11.GL_BLEND);
            if (enable) {
                GlStateManager._enableBlend();
            } else {
                GlStateManager._disableBlend();
            }
        }
    }

    public static void popBlend() {
        switch (lastBlendState) {
            case -1:
                LogHelper.bigError("[GLStateHelper] Attempt to pop before pushing");
                break;
            default:
                if (lastBlendState == 1) {
                    GlStateManager._enableBlend();

                } else {
                    GlStateManager._disableBlend();

                }
                lastBlendState = -1;
                break;
        }
    }

    private static int lastAlphaState = -1;

    public static void pushAlpha(boolean enable) {
        if (lastAlphaState != -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to push twice");
        } else {
            lastAlphaState = GlStateManager._getInteger(GL11.GL_ALPHA);
            if (enable) {
                GlStateManager._enableDepthTest();
            } else {
                GlStateManager._disableDepthTest();
            }
        }
    }

    public static void popAlpha() {
        if (lastAlphaState == -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to pop before pushing");
        } else {
            if (lastAlphaState == 1) {
                GlStateManager._enableDepthTest();
            } else {
                GlStateManager._disableDepthTest();
            }
            lastAlphaState = -1;
        }
    }

    private static int lastDepthState = -1;

    public static void pushDepth(boolean enable) {
        if (lastDepthState != -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to push twice");
        } else {
            lastDepthState = GlStateManager._getInteger(GL11.GL_DEPTH);
            if (enable) {
                GlStateManager._enableDepthTest();
            } else {
                GlStateManager._disableDepthTest();
            }
        }
    }

    public static void popDepth() {
        if (lastDepthState == -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to pop before pushing");
        } else {
            if (lastDepthState == 1) {
                GlStateManager._enableDepthTest();
            } else {
                GlStateManager._disableDepthTest();
            }
            lastDepthState = -1;
        }
    }
}