package net.thedragonteam.thedragonlib.items

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.util.*

class ItemTDLib : Item() {

    var nameMap: MutableMap<Int, String> = HashMap()

    fun addName(damage: Int, name: String): ItemTDLib {
        nameMap.put(damage, name)
        return this
    }

    override fun getUnlocalizedName(stack: ItemStack?): String {
        return if (getHasSubtypes() && nameMap.containsKey(stack!!.itemDamage)) super.getUnlocalizedName(stack) + "." + nameMap[stack.itemDamage] else super.getUnlocalizedName(stack)
    }
}