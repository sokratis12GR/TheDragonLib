package net.thedragonteam.thedragonlib.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

object NBTHelper {

    fun checkNBT(stack: ItemStack): ItemStack {
        if (stack.tagCompound == null)
            stack.tagCompound = NBTTagCompound()

        return stack
    }
}