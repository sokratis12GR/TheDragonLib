package net.thedragonteam.thedragonlib.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemDLib extends Item {

    public Map<Integer, String> nameMap = new HashMap<>();

    public ItemDLib addName(int damage, String name) {
        nameMap.put(damage, name);
        return this;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (getHasSubtypes() && nameMap.containsKey(stack.getItemDamage())) {
            return super.getUnlocalizedName(stack) + "." + nameMap.get(stack.getItemDamage());
        } else return super.getUnlocalizedName(stack);
    }
}