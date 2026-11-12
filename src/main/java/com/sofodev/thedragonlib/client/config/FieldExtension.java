package com.sofodev.thedragonlib.client.config;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Extra data a picker field carries after its id, e.g. {@code minecraft:diamond_sword[damage=5]} or
 * {@code minecraft:zombie{IsBaby:1b}}, edited in a screen of its own. The editor gives such a field a button next to its
 * input; suggestions, validation and names work on the id part alone, and picking another id keeps the data.
 * Set it on {@link RuleListContext}.
 *
 * @author Sokratis Fotkatzikis
 */
public interface FieldExtension {

    /** Whether this field of the rule type can carry data. */
    boolean appliesTo(RuleType type, int field);

    /** The id part of a value, without its data. */
    String id(String value);

    /** The value with its id replaced and its data kept. */
    String withId(String value, String id);

    /** Checks the data part; returns a translation key describing the problem, or null if it's valid or there's none. */
    @Nullable String validateData(RuleType type, int field, String value);

    Component buttonLabel(RuleType type, int field);

    Component buttonTooltip(RuleType type, int field);

    /**
     * Opens the data editor.
     *
     * @param onDone receives the whole new value (id and data); the rule editor rebuilds from it when it's shown again
     */
    void open(Screen parent, RuleType type, int field, String value, Consumer<String> onDone);

    /** Data can be long, so the input accepts more than a plain id. */
    default int maxLength() {
        return 32500;
    }
}
