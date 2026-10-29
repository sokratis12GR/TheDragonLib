package com.sofodev.thedragonlib.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Pairs a generated config values object with the {@link ModConfigSpec} it was built from.
 */
public record ConfigHolder<T>(T values, ModConfigSpec spec) {

    public ConfigHolder<T> register(ModContainer container, ModConfig.Type type) {
        container.registerConfig(type, spec);
        return this;
    }

    public ConfigHolder<T> register(ModContainer container, ModConfig.Type type, String fileName) {
        container.registerConfig(type, spec, fileName);
        return this;
    }
}
