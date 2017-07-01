package net.thedragonteam.thedragonlib.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.thedragonteam.thedragonlib.config.FeatureWrapper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockTDLib extends ItemBlock {

    public ItemBlockTDLib(Block block, FeatureWrapper feature) {
        super(block);
    }

    public ItemBlockTDLib(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(BlockTDLib.TILE_DATA_TAG)) {
            tooltip.add(I18n.format("info.tdl.has_saved_data.txt"));
        }
    }
}