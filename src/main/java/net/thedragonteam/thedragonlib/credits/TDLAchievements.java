/*
 * Copyright (c) TheDragonTeam 2016-2017.
 */

package net.thedragonteam.thedragonlib.credits;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.thedragonteam.thedragonlib.TheDragonLib;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.stats.AchievementList.OPEN_INVENTORY;
import static net.thedragonteam.thedragonlib.util.ItemStackUtils.getItemStack;

public class TDLAchievements {

    public static List<Achievement> thedragonlibAchievements = new ArrayList<>();

    public static void init() {
        thedragonlibAchievements.clear();
        thedragonlibAchievements.add(new AchievementTDL("thedragonlib", 0, 0, getItemStack(Items.ELYTRA), OPEN_INVENTORY).setSpecial());
        thedragonlibAchievements.add(new AchievementTDL("armorplus", 2, 0, getItemStack(Items.REDSTONE), OPEN_INVENTORY).setSpecial());
        thedragonlibAchievements.add(new AchievementTDL("mobsettings", 0, 2, getItemStack(Items.SKULL, 4), OPEN_INVENTORY).setSpecial());
        thedragonlibAchievements.add(new AchievementTDL("usefulrecipes", -2, 0, getItemStack(Blocks.CRAFTING_TABLE), OPEN_INVENTORY).setSpecial());

        AchievementPage tdlAchievementPage = new AchievementPage(TheDragonLib.MODNAME, AchievementTDL.achievements.toArray(new Achievement[AchievementTDL.achievements.size()]));
        AchievementPage.registerAchievementPage(tdlAchievementPage);
    }

    public static class AchievementTDL extends Achievement {
        public static List<Achievement> achievements = new ArrayList<>();

        public AchievementTDL(String name, int x, int y, ItemStack icon, Achievement parent) {
            super("achievement.thedragonlib." + name, "thedragonlib." + name, x, y, icon, parent);
            achievements.add(this);
            registerStat();
        }

        public AchievementTDL(String name, int x, int y, Item icon, Achievement parent) {
            this(name, x, y, getItemStack(icon), parent);
        }

        public AchievementTDL(String name, int x, int y, Block icon, Achievement parent) {
            this(name, x, y, getItemStack(icon), parent);
        }
    }
}