/*
 * Copyright (c) TheDragonTeam 2016-2017.
 */

package net.thedragonteam.thedragonlib.util

import net.minecraft.entity.Entity
import net.minecraft.util.EnumParticleTypes
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class ParticlesHelper {

    fun getParticle(name: String): EnumParticleTypes {
        return EnumParticleTypes.getByName(name) as EnumParticleTypes
    }

    fun getParticle(id: Int): EnumParticleTypes {
        return EnumParticleTypes.getParticleFromId(id) as EnumParticleTypes
    }

    enum class Pos {
        POS_X,
        POS_Y,
        POS_Z
    }

    companion object {

        fun spawnParticle(entity: Entity, particleType: EnumParticleTypes, xCoord: Double, yCoord: Double, zCoord: Double, xSpeed: Double = 0.0, ySpeed: Double = 0.0, zSpeed: Double = 0.0) {
            entity.world.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed)
        }

        fun spawnParticle(entity: Entity, particleType: EnumParticleTypes, xCoord: Double, yCoord: Double, zCoord: Double, allSpeed: Double) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, allSpeed, allSpeed, allSpeed)
        }

        fun spawnParticle(entity: Entity, particleType: EnumParticleTypes, xCoord: Double, yCoord: Double, zCoord: Double, speed: Double, ySpeed: Double, pos: Pos) {
            when (pos) {
                ParticlesHelper.Pos.POS_X -> spawnParticle(entity, particleType, xCoord, yCoord, zCoord, speed, ySpeed, 0.0)
                ParticlesHelper.Pos.POS_Z -> spawnParticle(entity, particleType, xCoord, yCoord, zCoord, 0.0, ySpeed, speed)
                ParticlesHelper.Pos.POS_Y -> TODO()
            }
        }

        fun spawnParticle(entity: Entity, particleType: EnumParticleTypes, xCoord: Double, yCoord: Double, zCoord: Double, xSpeed: Double, zSpeed: Double) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, xSpeed, 0.0, zSpeed)
        }

        fun spawnParticle(entity: Entity, particleType: EnumParticleTypes, xCoord: Double, yCoord: Double, zCoord: Double, xzSpeed: Double, xz: Boolean) {
            if (xz)
                spawnParticle(entity, particleType, xCoord, yCoord, zCoord, xzSpeed, xzSpeed)
        }

        fun spawnParticle(entity: Entity, particleType: EnumParticleTypes, xCoord: Double, yCoord: Double, zCoord: Double, speed: Double, pos: Pos) {
            when (pos) {
                ParticlesHelper.Pos.POS_X -> spawnParticle(entity, particleType, xCoord, yCoord, zCoord, speed, 0.0, 0.0)
                ParticlesHelper.Pos.POS_Y -> spawnParticle(entity, particleType, xCoord, yCoord, zCoord, 0.0, speed, 0.0)
                ParticlesHelper.Pos.POS_Z -> spawnParticle(entity, particleType, xCoord, yCoord, zCoord, 0.0, 0.0, speed)
            }
        }
    }
}
