package com.sofodev.thedragonlib.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;

/**
 * @author Sokratis Fotkatzikis
 */
public final class PoseStackHelper {

    private PoseStackHelper() {
    }

    public static void translateAndScale(PoseStack pose, Vec3 location, Vec3 scale) {
        pose.translate(location.x, location.y, location.z);
        pose.scale((float) scale.x, (float) scale.y, (float) scale.z);
    }

    public static void rotateItem(PoseStack pose, float xDegrees, float yDegrees, float zDegrees) {
        pose.rotateDegrees(Axis.XP, xDegrees);
        pose.rotateDegrees(Axis.YP, yDegrees);
        pose.rotateDegrees(Axis.ZP, zDegrees);
    }
}
