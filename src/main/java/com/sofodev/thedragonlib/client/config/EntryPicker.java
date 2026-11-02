package com.sofodev.thedragonlib.client.config;

import net.minecraft.client.gui.screens.Screen;

import java.util.List;
import java.util.function.Consumer;

/**
 * Opens a picker screen which produces new entries for a config list. Implementations decide
 * where candidates come from (a registry, a fixed set, ...); {@link EntryPickerScreen} is a
 * ready-made search/category screen they can delegate to.
 */
public interface EntryPicker {

    /** Whether the picker can be opened right now (e.g. a datapack registry needs a loaded world). */
    boolean isAvailable();

    /**
     * @param parent  the screen to return to
     * @param current the entries currently in the list
     * @param add     receives the entries to append
     */
    void open(Screen parent, List<?> current, Consumer<List<String>> add);
}
