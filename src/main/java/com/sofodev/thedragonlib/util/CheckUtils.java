package com.sofodev.thedragonlib.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Objects;

/**
 * @author Sokratis Fotkatzikis - TheDragonTeam
 */
public class CheckUtils {

    public static boolean isNotEmpty(ItemStack stack) {
        return !stack.isEmpty();
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack.isEmpty();
    }

    public static boolean isNotNull(Object object) {
        return object != null;
    }

    public static boolean areNotNull(Object object1, Object object2) {
        return object1 != null && object2 != null;
    }

    public static boolean isNotNullNorEmpty(String object) {
        return isNotNull(object) && !Objects.equals(object, "");
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNullOrEmpty(String object) {
        return isNull(object) || Objects.equals(object, "");
    }

    public static boolean areSame(ItemStack a, Item b) {
        return a.getItem() == b;
    }

    public static boolean isArmorEmpty(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        return (helmet.isEmpty() && chestplate.isEmpty() && leggings.isEmpty() && boots.isEmpty());
    }

    public static boolean isSame(ItemStack stack, Item item) {
        return !stack.isEmpty() && stack.getItem() == item;
    }
}
