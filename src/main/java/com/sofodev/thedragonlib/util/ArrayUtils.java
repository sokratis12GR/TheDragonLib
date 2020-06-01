package com.sofodev.thedragonlib.util;

import java.util.Arrays;
import java.util.Locale;

public class ArrayUtils {

    public static String[] arrayToLowercase(String[] array) {
        return Arrays.stream(array).map(s -> s.toLowerCase(Locale.ENGLISH)).toArray(String[]::new);
    }

    /**
     * Shift all objects in an array by the number of places specified.
     * <p/>
     * Example:
     * Object[] array = {1, 2, 3, 4, 5}
     * array = arrayShift(array, 1);
     * array now equals {5, 1, 2, 3, 4}
     * Shift can be a positive or negative number
     * <p/>
     * Shift is not restricted to any max or min value so it could for example be linked to a counter that
     * continuously increments.
     */
    public static Object[] arrayShift(Object[] input, int shift) {
        Object[] newArray = new Object[input.length];

        for (int i = 0; i < input.length; i++) {
            int newPos = (i + shift) % input.length;

            if (newPos < 0) newPos += input.length;

            newArray[newPos] = input[i];
        }

        return newArray;
    }
}