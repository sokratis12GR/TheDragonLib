package com.sofodev.thedragonlib.iface;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;


/**
 * @author Sokratis Fotkatzikis - TheDragonTeam
 **/
public interface IRarityHelper {

    default Rarity getRarity(String enumName, int formatting) {
        return Rarity.create(enumName, ChatFormatting.getById(formatting));
    }

    default Rarity getRarity(String enumName, ChatFormatting formatting) {
        return Rarity.create(enumName, formatting);
    }

    default Rarity getRarity(String enumName, String formatting) {
        return Rarity.create(enumName, ChatFormatting.getByName(formatting));
    }

    Rarity getRarity(ItemStack stack);

}
