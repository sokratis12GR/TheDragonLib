/*
 * Copyright (c) TheDragonTeam 2016-2017.
 */

package com.sofodev.thedragonlib.util;

import net.minecraft.entity.Entity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class ParticlesHelper {

    public static void spawnParticle(Entity entity, IParticleData particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {
        entity.world.addParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
    }

    public static void spawnParticle(Entity entity, IParticleData particleType, double xCoord, double yCoord, double zCoord) {
        spawnParticle(entity, particleType, xCoord, yCoord, zCoord, 0.0D, 0.0D, 0.0D);
    }

    public static void spawnParticle(Entity entity, IParticleData particleType, double xCoord, double yCoord, double zCoord, double allSpeed) {
        spawnParticle(entity, particleType, xCoord, yCoord, zCoord, allSpeed, allSpeed, allSpeed);
    }

    public static void spawnParticle(Entity entity, IParticleData particleType, double xCoord, double yCoord, double zCoord, double speed, double ySpeed, Pos pos) {
        if (pos == Pos.POS_X) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, speed, ySpeed, 0.0D);
        } else if (pos == Pos.POS_Z) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, 0.0D, ySpeed, speed);
        }
    }

    public static void spawnParticle(Entity entity, IParticleData particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double zSpeed) {
        spawnParticle(entity, particleType, xCoord, yCoord, zCoord, xSpeed, 0.0D, zSpeed);
    }

    public static void spawnParticle(Entity entity, IParticleData particleType, double xCoord, double yCoord, double zCoord, double xzSpeed, boolean xz) {
        if (xz) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, xzSpeed, xzSpeed);
        }
    }

    public static void spawnParticle(Entity entity, IParticleData particleType, double xCoord, double yCoord, double zCoord, double speed, Pos pos) {
        if (pos == Pos.POS_X) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, speed, 0.0D, 0.0D);
        } else if (pos == Pos.POS_Y) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, 0.0D, speed, 0.0D);
        } else if (pos == Pos.POS_Z) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, 0.0D, 0.0D, speed);
        }
    }

    public IParticleData getParticle(String name) {
        return (IParticleData) ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(name));
    }

    public enum Pos {
        POS_X,
        POS_Y,
        POS_Z,
        ;

        Pos() {
        }


    }
}
