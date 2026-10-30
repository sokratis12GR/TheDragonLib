package com.sofodev.thedragonlib.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

/**
 * Cuts out the {@code ModConfigSpec.Builder().configure(...)} boilerplate dependant mods would
 * otherwise repeat for every {@link ModConfig.Type}.
 */
public final class ConfigHelper {

    private ConfigHelper() {
    }

    public static <T> ConfigHolder<T> build(Function<ModConfigSpec.Builder, T> factory) {
        Pair<T, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(factory);
        return new ConfigHolder<>(pair.getLeft(), pair.getRight());
    }

    public static <T> ConfigHolder<T> buildAndRegister(ModContainer container, ModConfig.Type type, Function<ModConfigSpec.Builder, T> factory) {
        return build(factory).register(container, type);
    }

    public static <T> ConfigHolder<T> buildAndRegister(ModContainer container, ModConfig.Type type, String fileName, Function<ModConfigSpec.Builder, T> factory) {
        return build(factory).register(container, type, fileName);
    }

    /**
     * Reads {@code value} if {@code spec} has actually been loaded from disk, otherwise returns
     * {@code fallback}. Config values throw {@link IllegalStateException} when read before their
     * spec is loaded, which happens during datagen and other contexts where no config file is
     * ever loaded (e.g. a block's {@code isEnabled(FeatureFlagSet)} check running under the loot
     * table provider).
     */
    public static <V> V getOrDefault(ModConfigSpec spec, ModConfigSpec.ConfigValue<V> value, V fallback) {
        return spec.isLoaded() ? value.get() : fallback;
    }
}
