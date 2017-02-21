/*
 * Copyright (c) TheDragonTeam 2016-2017.
 */

package net.thedragonteam.thedragonlib.util

import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.items.ItemStackHandler

class ItemStackHandlerImproved(slots: Int) : ItemStackHandler(slots) {

    private val tempIgnoreConditions: Boolean = false

    val items: NonNullList<ItemStack>
        get() = this.stacks

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return if (!this.tempIgnoreConditions && !this.canInsert(stack, slot)) stack else super.insertItem(slot, stack, simulate)
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return if (!this.tempIgnoreConditions && !this.canExtract(this.getStackInSlot(slot), slot)) ItemStackUtils.getNull() else super.extractItem(slot, amount, simulate)
    }

    fun canInsert(stack: ItemStack, slot: Int): Boolean {
        return true
    }

    fun canExtract(stack: ItemStack, slot: Int): Boolean {
        return true
    }
}