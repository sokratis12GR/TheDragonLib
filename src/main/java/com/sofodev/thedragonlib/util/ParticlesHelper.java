/*
 * Copyright (c) TheDragonTeam 2016-2017.
 */

package com.sofodev.thedragonlib.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class ParticlesHelper {

    public static void spawnParticle(Entity entity, ParticleOptions particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {
        entity.level().addParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
    }

    public static void spawnParticle(Entity entity, ParticleOptions particleType, double xCoord, double yCoord, double zCoord) {
        spawnParticle(entity, particleType, xCoord, yCoord, zCoord, 0.0D, 0.0D, 0.0D);
    }

    public static void spawnParticle(Entity entity, ParticleOptions particleType, double xCoord, double yCoord, double zCoord, double allSpeed) {
        spawnParticle(entity, particleType, xCoord, yCoord, zCoord, allSpeed, allSpeed, allSpeed);
    }

    public static void spawnParticle(Entity entity, ParticleOptions particleType, double xCoord, double yCoord, double zCoord, double speed, double ySpeed, Pos pos) {
        if (pos == Pos.POS_X) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, speed, ySpeed, 0.0D);
        } else if (pos == Pos.POS_Z) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, 0.0D, ySpeed, speed);
        }
    }

    public static void spawnParticle(Entity entity, ParticleOptions particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double zSpeed) {
        spawnParticle(entity, particleType, xCoord, yCoord, zCoord, xSpeed, 0.0D, zSpeed);
    }

    public static void spawnParticle(Entity entity, ParticleOptions particleType, double xCoord, double yCoord, double zCoord, double xzSpeed, boolean xz) {
        if (xz) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, xzSpeed, xzSpeed);
        }
    }

    public static void spawnParticle(Entity entity, ParticleOptions particleType, double xCoord, double yCoord, double zCoord, double speed, Pos pos) {
        if (pos == Pos.POS_X) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, speed, 0.0D, 0.0D);
        } else if (pos == Pos.POS_Y) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, 0.0D, speed, 0.0D);
        } else if (pos == Pos.POS_Z) {
            spawnParticle(entity, particleType, xCoord, yCoord, zCoord, 0.0D, 0.0D, speed);
        }
    }

    public ParticleType<?> getParticle(String name) {
        return BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.parse(name));
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
