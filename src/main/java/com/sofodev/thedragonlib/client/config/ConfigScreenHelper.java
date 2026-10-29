package com.sofodev.thedragonlib.client.config;

import com.mojang.datafixers.util.Function4;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Function;

/**
 * Client-side only. Wires up a mod's config screen via the {@link IConfigScreenFactory}
 * extension point without pulling {@link Screen} into common code.
 */
public final class ConfigScreenHelper {

    private ConfigScreenHelper() {
    }

    public static void register(ModContainer container, IConfigScreenFactory factory) {
        container.registerExtensionPoint(IConfigScreenFactory.class, factory);
    }

    /**
     * Convenience for the common case of a screen that only needs its parent screen.
     */
    public static void register(ModContainer container, Function<Screen, Screen> factory) {
        register(container, (ctn, parent) -> factory.apply(parent));
    }

    /**
     * Wires up NeoForge's built-in {@link ConfigurationScreen}, auto-generated from the mod's
     * registered {@link net.neoforged.neoforge.common.ModConfigSpec}s, using a custom top-level
     * section screen (e.g. one that overrides {@code createList}/{@code createSection} to add
     * {@link EntryPicker} buttons to specific list entries).
     */
    public static void registerSectionScreen(ModContainer container,
                                              Function4<ConfigurationScreen, ModConfig.Type, ModConfig, Component, Screen> sectionFactory) {
        register(container, (mod, parent) -> new ConfigurationScreen(mod, parent, sectionFactory));
    }

    /**
     * Wires up NeoForge's built-in {@link ConfigurationScreen} with its default section screen.
     * Use this when none of the mod's config lists need an {@link EntryPicker}.
     */
    public static void registerDefaultScreen(ModContainer container) {
        register(container, (mod, parent) -> new ConfigurationScreen(mod, parent));
    }
}
