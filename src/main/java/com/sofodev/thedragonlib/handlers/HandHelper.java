package com.sofodev.thedragonlib.handlers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HandHelper {

    /**
     * Returns the first item found in ether of the players hands starting with the main hand
     */
    public static ItemStack getMainFirst(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        return !mainHand.isEmpty() ? mainHand : offHand;
    }

    /**
     * Returns the first item found in ether of the players hands starting with the off hand
     */
    public static ItemStack getOffFirst(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        return !offHand.isEmpty() ? offHand : mainHand;
    }

    /**
     * Returns the first item found in ether of the players hands that is the same as the given item
     */
    public static ItemStack getItem(Player player, Item item) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        if (!mainHand.isEmpty() && mainHand.getItem() == item) {
            return mainHand;
        } else if (!offHand.isEmpty() && offHand.getItem() == item) {
            return offHand;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getItemStack(Player player, ItemStack stack) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        if (!mainHand.isEmpty() && mainHand.getItem() == stack.getItem() && mainHand.getDamageValue() == stack.getDamageValue()) {
            return mainHand;
        } else if (!offHand.isEmpty() && offHand.getItem() == stack.getItem() && offHand.getDamageValue() == stack.getDamageValue()) {
            return offHand;
        }
        return ItemStack.EMPTY;
    }

    public static boolean isHoldingItemEther(Player player, Item item) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        return !mainHand.isEmpty() && mainHand.getItem() == item || !offHand.isEmpty() && offHand.getItem() == item;
    }
}