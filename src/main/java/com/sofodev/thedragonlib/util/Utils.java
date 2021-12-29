package com.sofodev.thedragonlib.util;

import com.sofodev.thedragonlib.lib.Vec3D;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class Utils {

    public static String formatNumber(double value) {
        if (value < 1000D) return String.valueOf(value);
        else if (value < 1000000D) return String.valueOf(Math.round(value) / 1000D) + "K";
        else if (value < 1000000000D) return String.valueOf(Math.round(value / 1000D) / 1000D) + "M";
        else if (value < 1000000000000D) return String.valueOf(Math.round(value / 1000000D) / 1000D) + "B";
        else return String.valueOf(Math.round(value / 1000000000D) / 1000D) + "T";
    }

    public static String formatNumber(long value) {
        if (value < 1000L) return String.valueOf(value);
        else if (value < 1000000L) return String.valueOf((value) / 1000D) + "K";
        else if (value < 1000000000L) return String.valueOf(((double) value / 1000L) / 1000D) + "M";
        else if (value < 1000000000000L) return String.valueOf(((double) value / 1000000L) / 1000D) + "B";
        else if (value < 1000000000000000L) return String.valueOf(((double) value / 1000000000L) / 1000D) + "T";
        else if (value < 1000000000000000000L)
            return String.valueOf(((double) value / 1000000000000L) / 1000D) + "Quad";
        else return String.valueOf(((double) value / 1000000000000000L) / 1000D) + "Quin";
    }

    /**
     * Calculates the exact distance between two points in 3D space
     *
     * @param x1 point A x
     * @param y1 point A y
     * @param z1 point A z
     * @param x2 point B x
     * @param y2 point B y
     * @param z2 point B z
     * @return The distance between point A and point B
     */
    public static double getDistanceAtoB(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return Math.sqrt((dx * dx + dy * dy + dz * dz));
    }

    public static double getDistanceAtoB(Vec3D pos1, Vec3D pos2) {
        return getDistanceAtoB(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
    }

    /**
     * Calculates the exact distance between two points in 2D space
     *
     * @param x1 point A x
     * @param z1 point A z
     * @param x2 point B x
     * @param z2 point B z
     * @return The distance between point A and point B
     */
    public static double getDistanceAtoB(double x1, double z1, double x2, double z2) {
        double dx = x1 - x2;
        double dz = z1 - z2;
        return Math.sqrt((dx * dx + dz * dz));
    }

    public static double getDistanceSq(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return dx * dx + dy * dy + dz * dz;
    }

    public static double getDistanceSq(double x1, double z1, double x2, double z2) {
        double dx = x1 - x2;
        double dz = z1 - z2;
        return dx * dx + dz * dz;
    }

//   /**
//    * Returns true if this is a client connected to a remote server.
//    */
//   public static boolean isConnectedToDedicatedServer() {
//       return FMLCommonLaunchHandler.getMinecraftServerInstance() == null;
//   }

    /**
     * Update the blocks an all 6 sides of a blocks.
     */
    public static void updateNeabourBlocks(Level world, BlockPos pos) {
//        world.notifyBlockOfStateChange(pos, world.getBlockState(pos).getBlock());
//        world.notifyBlockOfStateChange(pos.add(-1, 0, 0), world.getBlockState(pos).getBlock());
//        world.notifyBlockOfStateChange(pos.add(1, 0, 0), world.getBlockState(pos).getBlock());
//        world.notifyBlockOfStateChange(pos.add(0, -1, 0), world.getBlockState(pos).getBlock());
//        world.notifyBlockOfStateChange(pos.add(0, 1, 0), world.getBlockState(pos).getBlock());
//        world.notifyBlockOfStateChange(pos.add(0, 0, -1), world.getBlockState(pos).getBlock());
//        world.notifyBlockOfStateChange(pos.add(0, 0, 1), world.getBlockState(pos).getBlock());
    }

    public static double round(double number, double multiplier) {
        return Math.round(number * multiplier) / multiplier;
    }

    public static int getNearestMultiple(int number, int multiple) {
        int result = number;

        if (number < 0) result *= -1;

        if (result % multiple == 0) {
            return number;
        } else if (result % multiple < multiple / 2) {
            result -= result % multiple;
        } else result += (multiple - result % multiple);

        if (number < 0) result *= -1;

        return result;
    }

    /**
     * Simple method to convert a Double object to a primitive int
     */
    public static int toInt(double d) {
        return (int) d;
    }

    public static ResourceLocation toRL(String registryName) {
        return new ResourceLocation(registryName);
    }

    public static ResourceLocation toRL(String modid, String name) {
        return new ResourceLocation(modid, name);
    }
}