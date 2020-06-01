package com.sofodev.thedragonlib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.*;

@OnlyIn(Dist.CLIENT)
public class InfoHelper {

    public static final int GUI_TITLE = 0x00FFFF;

    public static void addLore(ItemStack stack, List<String> list, boolean addLeadingLine) {
        String[] lore = getLore(stack);
        if (addLeadingLine) list.add("");
        if (lore == null) {
            list.add("" + ITALIC + "" + DARK_PURPLE + "Invalid lore localization! (something is broken)");
            return;
        }
        Arrays.stream(lore).map(
            s -> "" + ITALIC + "" + DARK_PURPLE + s
        ).forEachOrdered(list::add);
    }

    /**
     * Add lore with a blank line above it
     */
    public static void addLore(ItemStack stack, List<String> list) {
        addLore(stack, list, true);
    }

    /**
     * Add the standard energy and lore information
     */
    public static void addEnergyAndLore(ItemStack stack, List<String> list) {
        if (!isShiftKeyDown()) {
            list.add(MessageFormat.format("{0} {1}{2}{3}{4} {5}{6}",
                I18n.format("info.sm.hold.txt"),
                AQUA, ITALIC, I18n.format("info.sm.shift.txt"), RESET,
                GRAY, I18n.format("info.sm.forDetails.txt")));
        } else {
            addLore(stack, list);
        }
    }

    /**
     * returns lore text or an empty string if the lore is not set
     */
    public static String[] getLore(ItemStack stack) {
        String unlocalizeLore = stack.getItem().getTranslationKey() + ".lore";
        String rawLore = I18n.format(unlocalizeLore);

        if (rawLore.contains(unlocalizeLore)) {
            //LogHelper.error("Invalid or missing Lore localization \""+unlocalizeLore+"\"");
            return new String[0];
        }

        String lineCountS = rawLore.substring(0, 1);
        rawLore = rawLore.substring(1);
        int lineCount = 0;

        try {
            lineCount = Integer.parseInt(lineCountS);
        } catch (NumberFormatException e) {
            LogHelper.error("Invalid Lore Format! Lore myst start with the number of lines \"3Line 1\\nLine 2\\nLine 3\"");
        }

        String[] loreLines = new String[lineCount];

        for (int i = 0; i < lineCount; i++) {
            loreLines[i] = rawLore.contains("\\n") ? rawLore.substring(0, rawLore.indexOf("\\n")) : rawLore;
            if (rawLore.contains("\\n")) rawLore = rawLore.substring(rawLore.indexOf("\\n") + 2);
        }

        return loreLines;
    }

    public static boolean isShiftKeyDown() {
        return GLFW.GLFW_PRESS == GLFW.glfwGetKey(Minecraft.getInstance().mainWindow.getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) ||
            GLFW.GLFW_PRESS == GLFW.glfwGetKey(Minecraft.getInstance().mainWindow.getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isCtrlKeyDown() {
        return GLFW.GLFW_PRESS == GLFW.glfwGetKey(Minecraft.getInstance().mainWindow.getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) ||
            GLFW.GLFW_PRESS == GLFW.glfwGetKey(Minecraft.getInstance().mainWindow.getHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean holdShiftForDetails(List<String> list, boolean inverted) {
        if (isShiftKeyDown() == inverted) {
            list.add(I18n.format("info.sc.holdShiftForDetails.txt", AQUA + "" + ITALIC, RESET + "" + GRAY));
        }
        return isShiftKeyDown();
    }

    public static boolean holdShiftForDetails(List<String> list) {
        return holdShiftForDetails(list, false);
    }

    /**
     * "Information Text Colour" The colour used for custom tool tip info
     */
    public static String ITC() {
        return "" + RESET + "" + TextFormatting.DARK_AQUA;
    }

    /**
     * "Highlighted Information Text Colour" The colour used for parts that need to stand out
     */
    public static String HITC() {
        return "" + RESET + "" + ITALIC + "" + TextFormatting.GOLD;
    }


}