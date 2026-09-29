package com.sofodev.thedragonlib.iface;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.Locale;


/**
 * @author Sokratis Fotkatzikis - TheDragonTeam
 **/
public interface IRarityHelper {

    default Rarity getRarity(String enumName, int formatting) {
        return getRarity(enumName, ChatFormatting.getById(formatting));
    }

    default Rarity getRarity(String enumName, ChatFormatting formatting) {
        // Dynamic Rarity.create(...) was removed in modern Minecraft/Forge.
        // First resolve a vanilla or statically enum-extended rarity by name.
        if (enumName != null) {
            try {
                return Rarity.valueOf(enumName.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                // Fall back to the closest vanilla rarity based on formatting.
            }
        }

        if (formatting == ChatFormatting.LIGHT_PURPLE) {
            return Rarity.EPIC;
        }
        if (formatting == ChatFormatting.AQUA) {
            return Rarity.RARE;
        }
        if (formatting == ChatFormatting.YELLOW) {
            return Rarity.UNCOMMON;
        }
        return Rarity.COMMON;
    }

    default Rarity getRarity(String enumName, String formatting) {
        return getRarity(enumName, ChatFormatting.getByName(formatting));
    }

    Rarity getRarity(ItemStack stack);

}
