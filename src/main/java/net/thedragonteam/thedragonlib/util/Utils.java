package net.thedragonteam.thedragonlib.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.thedragonteam.thedragonlib.lib.Vec3D;

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
        else if (value < 1000000L) return String.valueOf(Math.round(value) / 1000D) + "K";
        else if (value < 1000000000L) return String.valueOf(Math.round(value / 1000L) / 1000D) + "M";
        else if (value < 1000000000000L) return String.valueOf(Math.round(value / 1000000L) / 1000D) + "B";
        else if (value < 1000000000000000L) return String.valueOf(Math.round(value / 1000000000L) / 1000D) + "T";
        else if (value < 1000000000000000000L)
            return String.valueOf(Math.round(value / 1000000000000L) / 1000D) + "Quad";
        else return String.valueOf(Math.round(value / 1000000000000000L) / 1000D) + "Quin";
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
     *
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
     *
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

    /**
     * Returns true if this is a client connected to a remote server.
     */
    public static boolean isConnectedToDedicatedServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance() == null;
    }

    /**
     * Update the blocks an all 6 sides of a blocks.
     */
    public static void updateNeabourBlocks(World world, BlockPos pos) {
//        world.notifyBlockOfStateChange(pos, world.getBlockState(pos).getBlock());
//        world.notifyBlockOfStateChange(pos.add(-1, 0, 0), world.getBlockState(pos).getBlock());
//        world.notifyBlockOfStateChange(pos.add(1, 0, 0), world.getBlockState(pos).getBlock());
//        world.notifyBlockOfStateChange(pos.add(0, -1, 0), world.getBlockState(pos).getBlock());
//        world.notifyBlockOfStateChange(pos.add(0, 1, 0), world.getBlockState(pos).getBlock());
//        world.notifyBlockOfStateChange(pos.add(0, 0, -1), world.getBlockState(pos).getBlock());
//        world.notifyBlockOfStateChange(pos.add(0, 0, 1), world.getBlockState(pos).getBlock());
    }

    /**
     * Determine the orientation of a blocks based on the position of the entity that placed it.
     */
    public static int determineOrientation(int x, int y, int z, EntityLivingBase entity) {
        if (MathHelper.abs((float) entity.posX - (float) x) < 2.0F && MathHelper.abs((float) entity.posZ - (float) z) < 2.0F) {
            double d0 = entity.posY + 1.82D - (double) entity.getYOffset();

            if (d0 - (double) y > 2.0D) return 0;

            if ((double) y - d0 > 0.0D) return 1;
        }

        int l = MathHelper.floor((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 3 : (l == 1 ? 4 : (l == 2 ? 2 : (l == 3 ? 5 : 0)));
    }

    public static double round(double number, double multiplier) {
        return Math.round(number * multiplier) / multiplier;
    }

    public static int getNearestMultiple(int number, int multiple) {
        int result = number;

        if (number < 0) result *= -1;

        if (result % multiple == 0) return number;
        else if (result % multiple < multiple / 2) result = result - result % multiple;
        else result = result + (multiple - result % multiple);

        if (number < 0) result *= -1;

        return result;
    }

    /**
     * Simple method to convert a Double object to a primitive int
     */
    public static int toInt(double d) {
        return (int) d;
    }
}