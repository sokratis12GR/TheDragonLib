package com.sofodev.thedragonlib.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class ItemNBTHelper {

    /**
     * SETTERS
     */
    public static CompoundTag getCompound(ItemStack stack) {
        if (stack.getTag() == null) stack.setTag(new CompoundTag());
        return stack.getTag();
    }

    public static ItemStack putByte(ItemStack stack, String tag, byte b) {
        getCompound(stack).putByte(tag, b);
        return stack;
    }

    public static ItemStack putBoolean(ItemStack stack, String tag, boolean b) {
        getCompound(stack).putBoolean(tag, b);
        return stack;
    }

    public static ItemStack putShort(ItemStack stack, String tag, short s) {
        getCompound(stack).putShort(tag, s);
        return stack;
    }

    public static ItemStack putInteger(ItemStack stack, String tag, int i) {
        getCompound(stack).putInt(tag, i);
        return stack;
    }

    public static ItemStack putLong(ItemStack stack, String tag, long i) {
        getCompound(stack).putLong(tag, i);
        return stack;
    }

    public static ItemStack putFloat(ItemStack stack, String tag, float f) {
        getCompound(stack).putFloat(tag, f);
        return stack;
    }

    public static ItemStack putDouble(ItemStack stack, String tag, double d) {
        getCompound(stack).putDouble(tag, d);
        return stack;
    }

    public static ItemStack putString(ItemStack stack, String tag, String s) {
        getCompound(stack).putString(tag, s);
        return stack;
    }

    /**
     * GETTERS
     */
    public static boolean verifyExistence(ItemStack stack, String tag) {
        CompoundTag compound = stack.getTag();
        return compound != null && stack.getTag().hasUUID(tag);
    }

    public static byte getByte(ItemStack stack, String tag, byte defaultExpected) {
        return verifyExistence(stack, tag) ? stack.getTag().getByte(tag) : defaultExpected;
    }

    public static boolean getBoolean(ItemStack stack, String tag, boolean defaultExpected) {
        return verifyExistence(stack, tag) ? stack.getTag().getBoolean(tag) : defaultExpected;
    }

    public static short getShort(ItemStack stack, String tag, short defaultExpected) {
        return verifyExistence(stack, tag) ? stack.getTag().getShort(tag) : defaultExpected;
    }

    public static int getInteger(ItemStack stack, String tag, int defaultExpected) {
        return verifyExistence(stack, tag) ? stack.getTag().getInt(tag) : defaultExpected;
    }

    public static long getLong(ItemStack stack, String tag, long defaultExpected) {
        return verifyExistence(stack, tag) ? stack.getTag().getLong(tag) : defaultExpected;
    }

    public static float getFloat(ItemStack stack, String tag, float defaultExpected) {
        return verifyExistence(stack, tag) ? stack.getTag().getFloat(tag) : defaultExpected;
    }

    public static double getDouble(ItemStack stack, String tag, double defaultExpected) {
        return verifyExistence(stack, tag) ? stack.getTag().getDouble(tag) : defaultExpected;
    }

    public static String getString(ItemStack stack, String tag, String defaultExpected) {
        return verifyExistence(stack, tag) ? stack.getTag().getString(tag) : defaultExpected;
    }
}