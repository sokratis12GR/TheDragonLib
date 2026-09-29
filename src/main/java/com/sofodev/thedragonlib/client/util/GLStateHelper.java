package com.sofodev.thedragonlib.client.util;

import com.sofodev.thedragonlib.util.LogHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

public class GLStateHelper {

    private static int lastBlendState = -1;

    public static void pushBlend(boolean enable) {
        if (lastBlendState != -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to push twice");
        } else {
            lastBlendState = GL11.glIsEnabled(GL11.GL_BLEND) ? 1 : 0;
            if (enable) {
                GL11.glEnable(GL11.GL_BLEND);
            } else {
                GL11.glDisable(GL11.GL_BLEND);
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
                    GL11.glEnable(GL11.GL_BLEND);

                } else {
                    GL11.glDisable(GL11.GL_BLEND);

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
            lastAlphaState = GL11.glIsEnabled(GL11.GL_DEPTH_TEST) ? 1 : 0;
            if (enable) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            } else {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }
        }
    }

    public static void popAlpha() {
        if (lastAlphaState == -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to pop before pushing");
        } else {
            if (lastAlphaState == 1) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            } else {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }
            lastAlphaState = -1;
        }
    }

    private static int lastDepthState = -1;

    public static void pushDepth(boolean enable) {
        if (lastDepthState != -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to push twice");
        } else {
            lastDepthState = GL11.glIsEnabled(GL11.GL_DEPTH_TEST) ? 1 : 0;
            if (enable) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            } else {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }
        }
    }

    public static void popDepth() {
        if (lastDepthState == -1) {
            LogHelper.bigError("[GLStateHelper] Attempt to pop before pushing");
        } else {
            if (lastDepthState == 1) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            } else {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }
            lastDepthState = -1;
        }
    }
}