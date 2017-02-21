package net.thedragonteam.thedragonlib.handlers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

object HandHelper {

    /**
     * Returns the first item found in ether of the players hands starting with the main hand
     */
    fun getMainFirst(player: EntityPlayer): ItemStack {
        return if (!player.heldItemMainhand.isEmpty) player.heldItemMainhand else player.heldItemOffhand
    }

    /**
     * Returns the first item found in ether of the players hands starting with the off hand
     */
    fun getOffFirst(player: EntityPlayer): ItemStack {
        return if (!player.heldItemOffhand.isEmpty) player.heldItemOffhand else player.heldItemMainhand
    }

    /**
     * Returns the first item found in ether of the players hands that is the same as the given item
     */
    fun getItem(player: EntityPlayer, item: Item): ItemStack {
        if (!player.heldItemMainhand.isEmpty && player.heldItemMainhand.item === item)
            return player.heldItemMainhand
        else if (!player.heldItemOffhand.isEmpty && player.heldItemOffhand.item === item)
            return player.heldItemOffhand
        return ItemStack.EMPTY
    }

    fun getItemStack(player: EntityPlayer, itemStack: ItemStack): ItemStack {
        if (!player.heldItemMainhand.isEmpty && player.heldItemMainhand.item === itemStack.item && player.heldItemMainhand.itemDamage == itemStack.itemDamage)
            return player.heldItemMainhand
        else if (!player.heldItemOffhand.isEmpty && player.heldItemOffhand.item === itemStack.item && player.heldItemOffhand.itemDamage == itemStack.itemDamage)
            return player.heldItemOffhand
        return ItemStack.EMPTY
    }

    fun isHoldingItemEther(player: EntityPlayer, item: Item): Boolean {
        return !player.heldItemMainhand.isEmpty && player.heldItemMainhand.item === item || !player.heldItemOffhand.isEmpty && player.heldItemOffhand.item === item
    }
}