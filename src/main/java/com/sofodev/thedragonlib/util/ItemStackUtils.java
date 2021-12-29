/*
 * Copyright (c) TheDragonTeam 2016-2017.
 */

package com.sofodev.thedragonlib.util;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Collections;

import static java.util.Objects.requireNonNull;

public class ItemStackUtils {

    public static Item getItemByName(String name) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
    }

    public static ItemStack getTICItemStack(String name, int meta) {
        return getItemStack("tconstruct", name, meta);
    }

    public static ItemStack getAPItemStack(String name, int meta) {
        return getItemStack("armorplus", name, meta);
    }

    public static ItemStack getAPItemStack(String name) {
        return getAPItemStack(name, 0);
    }

    public static ItemStack getItemStack(String modid, String itemName, int meta) {
        return getItemStack(getItemByName(modid + ":" + itemName), meta);
    }

    public static ItemStack getItemStack(String modid, String itemName) {
        return getItemStack(modid, itemName, 0);
    }

    public static ItemStack getItemStack(Object type, int amount) {
        if (type instanceof String) {
            return new ItemStack(requireNonNull(getItemByName((String) type)), amount);
        } else if (type instanceof Block) {
            return new ItemStack((Block) type, amount);
        } else if (type instanceof Item) {
            return new ItemStack((Item) type, amount);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getItemStack(Object type) {
        if (type instanceof String) {
            return getItemStack(getItemByName((String) type), 0);
        } else if (type instanceof Block) {
            return getItemStack(type, 0);
        } else if (type instanceof Item) {
            return getItemStack(type, 0);
        } else if (type instanceof ItemStack) {
            return (ItemStack) type;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getEmptyStack() {
        return ItemStack.EMPTY;
    }

    public static Item getItem(Object type) {
        if (type instanceof String) {
            return getItemByName((String) type);
        } else if (type instanceof ItemStack) {
            return ((ItemStack) type).getItem();
        }
        return ItemStack.EMPTY.getItem();
    }

    public static Item getItem(String modName, String itemName) {
        return getItemByName(modName + ":" + itemName);
    }

    public static Item getEmptyItem() {
        return ItemStack.EMPTY.getItem();
    }

    public static ItemStack validateCopy(ItemStack stack) {
        return isValid(stack) ? stack.copy() : getNull();
    }

    public static ItemStack validateCheck(ItemStack stack) {
        return isValid(stack) ? stack : getNull();
    }

    public static boolean isValid(ItemStack stack) {
        return stack != null && !stack.isEmpty();
    }

    public static ItemStack getNull() {
        return ItemStack.EMPTY;
    }

    public static int getStackSize(ItemStack stack) {
        return !isValid(stack) ? 0 : stack.getCount();
    }

    public static ItemStack setStackSize(ItemStack stack, int size) {
        return setStackSize(stack, size, false);
    }

    public static ItemStack setStackSize(ItemStack stack, int size, boolean containerOnEmpty) {
        if (size <= 0) {
            return isValid(stack) && containerOnEmpty ? stack.getItem().getContainerItem(stack) : getNull();
        }
        stack.setCount(size);
        return stack;
    }

    public static ItemStack addStackSize(ItemStack stack, int size) {
        return addStackSize(stack, size, false);
    }

    public static ItemStack addStackSize(ItemStack stack, int size, boolean containerOnEmpty) {
        return setStackSize(stack, getStackSize(stack) + size, containerOnEmpty);
    }

    public static boolean isIInvEmpty(NonNullList<ItemStack> slots) {
        return slots.stream().noneMatch(ItemStackUtils::isValid);
    }

    public static NonNullList<ItemStack> createSlots(int size) {
        return NonNullList.withSize(size, getNull());
    }

    public static NonNullList<ItemStack> getItemStacks(Item... items) {
        NonNullList<ItemStack> list = NonNullList.create();
        Arrays.stream(items).map(ItemStackUtils::getItemStack).forEachOrdered(list::add);
        return list;
    }

    public static NonNullList<ItemStack> getItemStacks(ItemStack... itemStacks) {
        NonNullList<ItemStack> list = NonNullList.create();
        Collections.addAll(list, itemStacks);
        return list;
    }
}
