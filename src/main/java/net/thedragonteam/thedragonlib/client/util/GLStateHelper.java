package net.thedragonteam.thedragonlib.client.util;

import net.minecraft.client.renderer.GlStateManager;
import net.thedragonteam.thedragonlib.util.LogHelper;
import org.lwjgl.opengl.GL11;

public class GLStateHelper {

    private static int lastBlendState = -1;

    public static void pushBlend(boolean enable) {
        if (lastBlendState != -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to push twice");
        } else {
            lastBlendState = GlStateManager.glGetInteger(GL11.GL_BLEND);
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
                switch (lastBlendState) {
                    case 1:
                        GlStateManager.enableBlend();
                        break;
                    default:
                        GlStateManager.disableBlend();
                        break;
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
            lastAlphaState = GlStateManager.glGetInteger(GL11.GL_ALPHA);
            if (enable) {
                GlStateManager.enableAlpha();
            } else {
                GlStateManager.disableAlpha();
            }
        }
    }

    public static void popAlpha() {
        if (lastAlphaState == -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to pop before pushing");
        } else {
            if (lastAlphaState == 1) {
                GlStateManager.enableAlpha();
            } else {
                GlStateManager.disableAlpha();
            }
            lastAlphaState = -1;
        }
    }

    private static int lastDepthState = -1;

    public static void pushDepth(boolean enable) {
        if (lastDepthState != -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to push twice");
        } else {
            lastDepthState = GlStateManager.glGetInteger(GL11.GL_DEPTH);
            if (enable) {
                GlStateManager.enableDepth();
            } else {
                GlStateManager.disableDepth();
            }
        }
    }

    public static void popDepth() {
        if (lastDepthState == -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to pop before pushing");
        } else {
            if (lastDepthState == 1) {
                GlStateManager.enableDepth();
            } else {
                GlStateManager.disableDepth();
            }
            lastDepthState = -1;
        }
    }
}