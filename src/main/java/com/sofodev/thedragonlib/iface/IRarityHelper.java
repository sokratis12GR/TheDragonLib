package com.sofodev.thedragonlib.iface;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.TextFormatting;

import static net.minecraft.util.text.TextFormatting.fromColorIndex;
import static net.minecraft.util.text.TextFormatting.getValueByName;

/**
 * @author Sokratis Fotkatzikis - TheDragonTeam
 **/
public interface IRarityHelper {

    default Rarity getRarity(String enumName, int formatting) {
        return Rarity.create(enumName, fromColorIndex(formatting));
    }

    default Rarity getRarity(String enumName, TextFormatting formatting) {
        return Rarity.create(enumName, formatting);
    }

    default Rarity getRarity(String enumName, String formatting) {
        return Rarity.create(enumName, getValueByName(formatting));
    }

    Rarity getRarity(ItemStack stack);

}
