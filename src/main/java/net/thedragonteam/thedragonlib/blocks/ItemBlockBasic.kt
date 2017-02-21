package net.thedragonteam.thedragonlib.blocks

import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.thedragonteam.thedragonlib.config.FeatureWrapper

class ItemBlockBasic(block: Block) : ItemBlockTDLib(block) {

    private lateinit var feature: FeatureWrapper

    constructor(block: Block, feature: FeatureWrapper) : this(block) {
        this.feature = feature
        this.setHasSubtypes(feature.variantMap().isNotEmpty())
    }

    override fun getUnlocalizedName(stack: ItemStack?): String {
        return if (getHasSubtypes() && feature.nameMap.containsKey(stack!!.itemDamage)) (super.getUnlocalizedName(stack) + "." + feature.nameMap[stack.itemDamage]).replace("=".toRegex(), ".").replace(",".toRegex(), ".") else super.getUnlocalizedName(stack)
    }
}