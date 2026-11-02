package com.sofodev.thedragonlib.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Client-side only. Wires up a mod's config screen via the
 * {@link ConfigScreenHandler.ConfigScreenFactory} extension point without pulling {@link Screen}
 * into common code.
 */
public final class ConfigScreenHelper {

    private ConfigScreenHelper() {
    }

    public static void register(ModContainer container, BiFunction<Minecraft, Screen, Screen> factory) {
        container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(factory));
    }

    /**
     * Convenience for the common case of a screen that only needs its parent screen.
     */
    public static void register(ModContainer container, Function<Screen, Screen> factory) {
        container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(factory));
    }

    /**
     * Wires up a {@link ConfigSpecScreen} for {@code spec}. List-typed entries get a plain
     * {@link StringListScreen}; pass a {@code listScreenFactory} to use a {@link PickerListScreen}
     * (or any other screen) for specific keys instead.
     */
    public static void registerConfigScreen(ModContainer container, Component title, ForgeConfigSpec spec) {
        registerConfigScreen(container, title, spec, null);
    }

    public static void registerConfigScreen(ModContainer container, Component title, ForgeConfigSpec spec,
                                              ConfigSpecScreen.@Nullable ListScreenFactory listScreenFactory) {
        register(container, parent -> new ConfigSpecScreen(title, parent, spec, listScreenFactory));
    }
}
