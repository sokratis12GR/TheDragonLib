package net.thedragonteam.thedragonlib.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.*;

@SideOnly(Side.CLIENT)
public class InfoHelper {

    public static final int GUI_TITLE = 0x00FFFF;

    public static void addLore(ItemStack stack, List<String> list, boolean addLeadingLine) {
        String[] lore = getLore(stack);
        if (addLeadingLine) list.add("");
        if (lore == null) {
            list.add("" + ITALIC + "" + TextFormatting.DARK_PURPLE + "Invalid lore localization! (something is broken)");
            return;
        }
        Arrays.stream(lore).map(
            s -> "" + ITALIC + "" + TextFormatting.DARK_PURPLE + s
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
        String unlocalizeLore = stack.getItem().getUnlocalizedName() + ".lore";
        String rawLore = I18n.format(unlocalizeLore);

        if (rawLore.contains(unlocalizeLore)) {
            //LogHelper.error("Invalid or missing Lore localization \""+unlocalizeLore+"\"");
            return null;
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
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    public static boolean isCtrlKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
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