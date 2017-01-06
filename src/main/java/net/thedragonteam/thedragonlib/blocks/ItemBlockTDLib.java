package net.thedragonteam.thedragonlib.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thedragonteam.thedragonlib.config.FeatureWrapper;

import java.util.List;

public class ItemBlockTDLib extends ItemBlock {

    public ItemBlockTDLib(Block block, FeatureWrapper feature) {
        super(block);
    }

    public ItemBlockTDLib(Block block) {
        super(block);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(BlockTDLib.TILE_DATA_TAG)) {
            tooltip.add(I18n.format("info.tdl.has_saved_data.txt"));
        }
    }
}