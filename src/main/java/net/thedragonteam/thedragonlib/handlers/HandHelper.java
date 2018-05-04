package net.thedragonteam.thedragonlib.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class HandHelper {

    /**
     * Returns the first item found in ether of the players hands starting with the main hand
     */
    public static ItemStack getMainFirst(EntityPlayer player) {
        return !player.getHeldItemMainhand().isEmpty() ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
    }

    /**
     * Returns the first item found in ether of the players hands starting with the off hand
     */
    public static ItemStack getOffFirst(EntityPlayer player) {
        return !player.getHeldItemOffhand().isEmpty() ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
    }

    /**
     * Returns the first item found in ether of the players hands that is the same as the given item
     */
    public static ItemStack getItem(EntityPlayer player, Item item) {
        if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == item) {
            return player.getHeldItemMainhand();
        } else if (!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() == item) {
            return player.getHeldItemOffhand();
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getItemStack(EntityPlayer player, ItemStack itemStack) {
        if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == itemStack.getItem() && player.getHeldItemMainhand().getItemDamage() == itemStack.getItemDamage()) {
            return player.getHeldItemMainhand();
        } else if (!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() == itemStack.getItem() && player.getHeldItemOffhand().getItemDamage() == itemStack.getItemDamage()) {
            return player.getHeldItemOffhand();
        }
        return ItemStack.EMPTY;
    }

    public static boolean isHoldingItemEther(EntityPlayer player, Item item) {
        return !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == item || !player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() == item;
    }
}