package com.sofodev.thedragonlib.util;
//General place to call text formatting from. You can alternatively use 'EnumChatFormatting'.


import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class TextHelper {
    public static String getFormattedText(String string) {
        return string.replaceAll("&", "\u00A7");
    }

    public static String localize(String input, Object... format) {
        return I18n.get(input, format);
    }

    public static String localizeEffect(String input, Object... format) {
        return getFormattedText(localize(input, format));
    }

    public static String[] localizeAll(String[] input) {
        return Arrays.stream(input).map(TextHelper::localize).toArray(String[]::new);
    }

    public static String[] localizeAllEffect(String[] input) {
        return Arrays.stream(input).map(TextHelper::localizeEffect).toArray(String[]::new);
    }

    public static List<String> localizeAll(List<String> input) {
        ArrayList<String> ret = new ArrayList<>(input.size());
        IntStream.range(0, input.size()).forEachOrdered(i -> ret.add(i, localize(input.get(i))));
        return ret;
    }

    public static List<String> localizeAllEffect(List<String> input) {
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