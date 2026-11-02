package com.sofodev.thedragonlib.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Pairs a generated config values object with the {@link ForgeConfigSpec} it was built from.
 */
public record ConfigHolder<T>(T values, ForgeConfigSpec spec) {

    public ConfigHolder<T> register(ModLoadingContext context, ModConfig.Type type) {
        context.registerConfig(type, spec);
        return this;
    }

    public ConfigHolder<T> register(ModLoadingContext context, ModConfig.Type type, String fileName) {
        context.registerConfig(type, spec, fileName);
        return this;
    }

    public boolean isLoaded() {
        return spec.isLoaded();
    }

    /**
     * @see ConfigHelper#getOrDefault(ForgeConfigSpec, ForgeConfigSpec.ConfigValue, Object)
     */
    public <V> V getOrDefault(ForgeConfigSpec.ConfigValue<V> value, V fallback) {
        return ConfigHelper.getOrDefault(spec, value, fallback);
    }
}
