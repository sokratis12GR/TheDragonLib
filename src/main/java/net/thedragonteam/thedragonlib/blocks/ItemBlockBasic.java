package net.thedragonteam.thedragonlib.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.thedragonteam.thedragonlib.config.FeatureWrapper;

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

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (getHasSubtypes() && feature.nameMap.containsKey(stack.getItemDamage())) {
            return (super.getUnlocalizedName(stack) + "." + feature.nameMap.get(stack.getItemDamage())).replaceAll("=", ".").replaceAll(",", ".");
        } else return super.getUnlocalizedName(stack);
    }
}