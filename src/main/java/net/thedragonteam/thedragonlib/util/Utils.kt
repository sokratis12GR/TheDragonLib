package net.thedragonteam.thedragonlib.util

import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fml.common.FMLCommonHandler
import net.thedragonteam.thedragonlib.lib.Vec3D

object Utils {

    fun formatNumber(value: Double): String {
        when {
            value < 1000.0 -> return value.toString()
            value < 1000000.0 -> return (Math.round(value) / 1000.0).toString() + "K"
            value < 1000000000.0 -> return (Math.round(value / 1000.0) / 1000.0).toString() + "M"
            value < 1000000000000.0 -> return (Math.round(value / 1000000.0) / 1000.0).toString() + "B"
            else -> return (Math.round(value / 1000000000.0) / 1000.0).toString() + "T"
        }
    }

    fun formatNumber(value: Long): String {
        when {
            value < 1000L -> return value.toString()
            value < 1000000L -> return (Math.round(value.toFloat()) / 1000.0).toString() + "K"
            value < 1000000000L -> return (Math.round((value / 1000L).toFloat()) / 1000.0).toString() + "M"
            value < 1000000000000L -> return (Math.round((value / 1000000L).toFloat()) / 1000.0).toString() + "B"
            value < 1000000000000000L -> return (Math.round((value / 1000000000L).toFloat()) / 1000.0).toString() + "T"
            value < 1000000000000000000L -> return (Math.round((value / 1000000000000L).toFloat()) / 1000.0).toString() + "Quad"
            else -> return (Math.round((value / 1000000000000000L).toFloat()) / 1000.0).toString() + "Quin"
        }
    }

    /**
     * Calculates the exact distance between two points in 3D space

     * @param x1 point A x
     * *
     * @param y1 point A y
     * *
     * @param z1 point A z
     * *
     * @param x2 point B x
     * *
     * @param y2 point B y
     * *
     * @param z2 point B z
     * *
     * *
     * @return The distance between point A and point B
     */
    fun getDistanceAtoB(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
        val dx = x1 - x2
        val dy = y1 - y2
        val dz = z1 - z2
        return Math.sqrt(dx * dx + dy * dy + dz * dz)
    }

    fun getDistanceAtoB(pos1: Vec3D, pos2: Vec3D): Double {
        return getDistanceAtoB(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z)
    }

    /**
     * Calculates the exact distance between two points in 2D space

     * @param x1 point A x
     * *
     * @param z1 point A z
     * *
     * @param x2 point B x
     * *
     * @param z2 point B z
     * *
     * *
     * @return The distance between point A and point B
     */
    fun getDistanceAtoB(x1: Double, z1: Double, x2: Double, z2: Double): Double {
        val dx = x1 - x2
        val dz = z1 - z2
        return Math.sqrt(dx * dx + dz * dz)
    }

    fun getDistanceSq(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
        val dx = x1 - x2
        val dy = y1 - y2
        val dz = z1 - z2
        return dx * dx + dy * dy + dz * dz
    }

    fun getDistanceSq(x1: Double, z1: Double, x2: Double, z2: Double): Double {
        val dx = x1 - x2
        val dz = z1 - z2
        return dx * dx + dz * dz
    }

    /**
     * Returns true if this is a client connected to a remote server.
     */
    val isConnectedToDedicatedServer: Boolean
        get() = FMLCommonHandler.instance().minecraftServerInstance == null

    /**
     * Update the blocks an all 6 sides of a blocks.
     */
    fun updateNeabourBlocks(world: World, pos: BlockPos) {
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
    fun determineOrientation(x: Int, y: Int, z: Int, entity: EntityLivingBase): Int {
        if (MathHelper.abs(entity.posX.toFloat() - x.toFloat()) < 2.0f && MathHelper.abs(entity.posZ.toFloat() - z.toFloat()) < 2.0f) {
            val d0 = entity.posY + 1.82 - entity.yOffset.toDouble()

            if (d0 - y.toDouble() > 2.0) return 0

            if (y.toDouble() - d0 > 0.0) return 1
        }

        val l = MathHelper.floor((entity.rotationYaw * 4.0f / 360.0f).toDouble() + 0.5) and 3
        return if (l == 0) 3 else if (l == 1) 4 else if (l == 2) 2 else if (l == 3) 5 else 0
    }

    fun round(number: Double, multiplier: Double): Double {
        return Math.round(number * multiplier) / multiplier
    }

    fun getNearestMultiple(number: Int, multiple: Int): Int {
        var result = number

        if (number < 0) result *= -1

        when {
            result % multiple == 0 -> return number
            result % multiple < multiple / 2 -> result = result - result % multiple
            else -> result = result + (multiple - result % multiple)
        }

        if (number < 0) result *= -1

        return result
    }

    /**
     * Simple method to convert a Double object to a primitive int
     */
    fun toInt(d: Double): Int {
        return d.toInt()
    }
}