package net.thedragonteam.thedragonlib.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class HandHelper {

    /**
     * Returns the first item found in ether of the players hands starting with the main hand
     */
    public static ItemStack getMainFirst(EntityPlayer player) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        return !mainHand.isEmpty() ? mainHand : offHand;
    }

    /**
     * Returns the first item found in ether of the players hands starting with the off hand
     */
    public static ItemStack getOffFirst(EntityPlayer player) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        return !offHand.isEmpty() ? offHand :mainHand;
    }

    /**
     * Returns the first item found in ether of the players hands that is the same as the given item
     */
    public static ItemStack getItem(EntityPlayer player, Item item) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        if (!mainHand.isEmpty() && mainHand.getItem() == item) {
            return mainHand;
        } else if (!offHand.isEmpty() && offHand.getItem() == item) {
            return offHand;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getItemStack(EntityPlayer player, ItemStack stack) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        if (!mainHand.isEmpty() && mainHand.getItem() == stack.getItem() && mainHand.getItemDamage() == stack.getItemDamage()) {
            return mainHand;
        } else if (!offHand.isEmpty() && offHand.getItem() == stack.getItem() && offHand.getItemDamage() == stack.getItemDamage()) {
            return offHand;
        }
        return ItemStack.EMPTY;
    }

    public static boolean isHoldingItemEther(EntityPlayer player, Item item) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        return !mainHand.isEmpty() && mainHand.getItem() == item || !offHand.isEmpty() && offHand.getItem() == item;
    }
}