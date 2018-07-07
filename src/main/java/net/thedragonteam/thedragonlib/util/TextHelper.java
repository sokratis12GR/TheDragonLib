package net.thedragonteam.thedragonlib.util;
//General place to call text formatting from. You can alternatively use 'EnumChatFormatting'.


import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@SideOnly(Side.CLIENT)
public class TextHelper {
    public static String getFormattedText(String string) {
        return string.replaceAll("&", "\u00A7");
    }

    public static String localize(String input, Object... format) {
        return I18n.format(input, format);
    }

    public static String localizeEffect(String input, Object... format) {
        return getFormattedText(localize(input, format));
    }

    public static String[] localizeAll(String[] input) {
        return Arrays.stream(input).map(s -> localize(s)).toArray(String[]::new);
    }

    public static String[] localizeAllEffect(String[] input) {
        return Arrays.stream(input).map(s -> localizeEffect(s)).toArray(String[]::new);
    }

    public static ArrayList<String> localizeAll(List<String> input) {
        ArrayList<String> ret = new ArrayList<>(input.size());
        IntStream.range(0, input.size()).forEachOrdered(i -> ret.add(i, localize(input.get(i))));
        return ret;
    }

    public static ArrayList<String> localizeAllEffect(List<String> input) {
        ArrayList<String> ret = new ArrayList<>(input.size());
        IntStream.range(0, input.size()).forEachOrdered(i -> ret.add(i, localizeEffect(input.get(i))));
        return ret;
    }

    public static String[] cutLongString(String string, int characters) {
        return WordUtils.wrap(string, characters, "/cut", false).split("/cut");
    }

    public static String[] cutLongString(String string) {
        return cutLongString(string, 30);
    }
}