package net.thedragonteam.thedragonlib.blocks

import net.minecraft.block.Block
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.thedragonteam.thedragonlib.config.FeatureWrapper

open class ItemBlockTDLib : ItemBlock {

    constructor(block: Block, feature: FeatureWrapper) : super(block) {
    }

    constructor(block: Block) : super(block) {
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: MutableList<String>, advanced: Boolean) {
        super.addInformation(stack, playerIn, tooltip, advanced)
        if (stack.hasTagCompound() && stack.tagCompound!!.hasKey(BlockTDLib.TILE_DATA_TAG)) {
            tooltip.add(I18n.format("info.tdl.has_saved_data.txt"))
        }
    }
}