package net.thedragonteam.thedragonlib.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemTDLib extends Item {

    public Map<Integer, String> nameMap = new HashMap<>();

    public ItemTDLib addName(int damage, String name) {
        nameMap.put(damage, name);
        return this;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getHasSubtypes() && nameMap.containsKey(stack.getItemDamage()) ? super.getUnlocalizedName(stack) + "." + nameMap.get(stack.getItemDamage()) : super.getUnlocalizedName(stack);
    }
}