package com.sofodev.thedragonlib.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

/**
 * Cuts out the {@code ForgeConfigSpec.Builder().configure(...)} boilerplate for every
 * {@link ModConfig.Type}.
 */
public final class ConfigHelper {

    private ConfigHelper() {
    }

    public static <T> ConfigHolder<T> build(Function<ForgeConfigSpec.Builder, T> factory) {
        Pair<T, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(factory);
        return new ConfigHolder<>(pair.getLeft(), pair.getRight());
    }

    public static <T> ConfigHolder<T> buildAndRegister(ModLoadingContext context, ModConfig.Type type, Function<ForgeConfigSpec.Builder, T> factory) {
        return build(factory).register(context, type);
    }

    public static <T> ConfigHolder<T> buildAndRegister(ModLoadingContext context, ModConfig.Type type, String fileName, Function<ForgeConfigSpec.Builder, T> factory) {
        return build(factory).register(context, type, fileName);
    }

    /**
     * Reads {@code value} if {@code spec} has been loaded from disk, otherwise returns
     * {@code fallback}. Config values throw {@link IllegalStateException} when read before their
     * spec is loaded, e.g. during datagen.
     */
    public static <V> V getOrDefault(ForgeConfigSpec spec, ForgeConfigSpec.ConfigValue<V> value, V fallback) {
        return spec.isLoaded() ? value.get() : fallback;
    }
}
