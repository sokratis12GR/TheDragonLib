package net.thedragonteam.thedragonlib.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class HandHelper {

    /**
     * Returns the first item found in ether of the players hands starting with the main hand
     */
    public static ItemStack getMainFirst(EntityPlayer player) {
        if (player.getHeldItemMainhand() != null) return player.getHeldItemMainhand();
        else return player.getHeldItemOffhand();
    }

    /**
     * Returns the first item found in ether of the players hands starting with the off hand
     */
    public static ItemStack getOffFirst(EntityPlayer player) {
        if (player.getHeldItemOffhand() != null) return player.getHeldItemOffhand();
        else return player.getHeldItemMainhand();
    }

    /**
     * Returns the first item found in ether of the players hands that is the same as the given item
     */
    public static ItemStack getItem(EntityPlayer player, Item item) {
        if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == item) {
            return player.getHeldItemMainhand();
        } else if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() == item) {
            return player.getHeldItemOffhand();
        }
        return null;
    }

    public static ItemStack getItemStack(EntityPlayer player, ItemStack itemStack) {
        if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == itemStack.getItem() && player.getHeldItemMainhand().getItemDamage() == itemStack.getItemDamage()) {
            return player.getHeldItemMainhand();
        } else if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() == itemStack.getItem() && player.getHeldItemOffhand().getItemDamage() == itemStack.getItemDamage()) {
            return player.getHeldItemOffhand();
        }
        return null;
    }

    public static boolean isHoldingItemEther(EntityPlayer player, Item item) {
        if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == item) {
            return true;
        } else if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() == item) {
            return true;
        } else {
            return false;
        }
    }
}