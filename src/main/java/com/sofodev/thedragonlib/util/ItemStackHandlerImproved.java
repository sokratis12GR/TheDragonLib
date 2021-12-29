/*
 * Copyright (c) TheDragonTeam 2016-2017.
 */

package com.sofodev.thedragonlib.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ItemStackHandlerImproved extends ItemStackHandler {

    private boolean tempIgnoreConditions;

    public ItemStackHandlerImproved(int slots) {
        super(slots);
    }

    public NonNullList<ItemStack> getItems() {
        return this.stacks;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return !this.tempIgnoreConditions && !this.canInsert(stack, slot) ? stack : super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return !this.tempIgnoreConditions && !this.canExtract(this.getStackInSlot(slot), slot) ? ItemStackUtils.getNull() : super.extractItem(slot, amount, simulate);
    }

    public boolean canInsert(ItemStack stack, int slot) {
        return true;
    }

    public boolean canExtract(ItemStack stack, int slot) {
        return true;
    }
}