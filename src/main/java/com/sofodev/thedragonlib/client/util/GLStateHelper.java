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
            lastBlendState = GlStateManager.getInteger(GL11.GL_BLEND);
            if (enable) {
                GlStateManager.enableBlend();
            } else {
                GlStateManager.disableBlend();
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
                    GlStateManager.enableBlend();

                } else {
                    GlStateManager.disableBlend();

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
            lastAlphaState = GlStateManager.getInteger(GL11.GL_ALPHA);
            if (enable) {
                GlStateManager.enableAlphaTest();
            } else {
                GlStateManager.disableAlphaTest();
            }
        }
    }

    public static void popAlpha() {
        if (lastAlphaState == -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to pop before pushing");
        } else {
            if (lastAlphaState == 1) {
                GlStateManager.enableAlphaTest();
            } else {
                GlStateManager.disableAlphaTest();
            }
            lastAlphaState = -1;
        }
    }

    private static int lastDepthState = -1;

    public static void pushDepth(boolean enable) {
        if (lastDepthState != -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to push twice");
        } else {
            lastDepthState = GlStateManager.getInteger(GL11.GL_DEPTH);
            if (enable) {
                GlStateManager.enableAlphaTest();
            } else {
                GlStateManager.disableAlphaTest();
            }
        }
    }

    public static void popDepth() {
        if (lastDepthState == -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to pop before pushing");
        } else {
            if (lastDepthState == 1) {
                GlStateManager.enableAlphaTest();
            } else {
                GlStateManager.disableAlphaTest();
            }
            lastDepthState = -1;
        }
    }
}