package net.thedragonteam.thedragonlib.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.thedragonteam.thedragonlib.config.FeatureWrapper;

import javax.annotation.Nonnull;

public class ItemBlockBasic extends ItemBlockTDLib {

    private FeatureWrapper feature;

    public ItemBlockBasic(Block block) {
        super(block);
    }

    public ItemBlockBasic(Block block, FeatureWrapper feature) {
        this(block);
        this.feature = feature;
        this.setHasSubtypes(feature.variantMap().length > 0);
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getHasSubtypes() && feature.nameMap.containsKey(stack.getItemDamage()) ? (super.getUnlocalizedName(stack) + "." + feature.nameMap.get(stack.getItemDamage())).replaceAll("=", ".").replaceAll(",", ".") : super.getUnlocalizedName(stack);
    }
}