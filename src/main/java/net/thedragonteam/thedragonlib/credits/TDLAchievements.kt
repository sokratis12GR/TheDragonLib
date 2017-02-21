/*
 * Copyright (c) TheDragonTeam 2016-2017.
 */

package net.thedragonteam.thedragonlib.credits

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.stats.Achievement
import net.minecraft.stats.AchievementList.OPEN_INVENTORY
import net.minecraftforge.common.AchievementPage
import net.thedragonteam.thedragonlib.TheDragonLib
import net.thedragonteam.thedragonlib.util.ItemStackUtils.getItemStack

object TDLAchievements {

    var thedragonlibAchievements: MutableList<Achievement> = mutableListOf()

    fun init() {
        thedragonlibAchievements.clear()
        thedragonlibAchievements.add(AchievementTDL("thedragonlib", 0, 0, getItemStack(Items.ELYTRA), OPEN_INVENTORY).setSpecial())
        thedragonlibAchievements.add(AchievementTDL("armorplus", 2, 0, getItemStack(Items.REDSTONE), OPEN_INVENTORY).setSpecial())
        thedragonlibAchievements.add(AchievementTDL("mobsettings", 0, 2, getItemStack(Items.SKULL, 4), OPEN_INVENTORY).setSpecial())
        thedragonlibAchievements.add(AchievementTDL("usefulrecipes", -2, 0, getItemStack(Blocks.CRAFTING_TABLE), OPEN_INVENTORY).setSpecial())

        val tdlAchievementPage = AchievementPage(TheDragonLib.MODNAME, *AchievementTDL.achievements.toTypedArray())
        AchievementPage.registerAchievementPage(tdlAchievementPage)
    }

    class AchievementTDL(name: String, x: Int, y: Int, icon: ItemStack, parent: Achievement) : Achievement("achievement.thedragonlib." + name, "thedragonlib." + name, x, y, icon, parent) {

        init {
            achievements.add(this)
            registerStat()
        }

        constructor(name: String, x: Int, y: Int, icon: Item, parent: Achievement) : this(name, x, y, getItemStack(icon), parent) {
        }

        constructor(name: String, x: Int, y: Int, icon: Block, parent: Achievement) : this(name, x, y, getItemStack(icon), parent) {
        }

        companion object {
            var achievements: MutableList<Achievement> = mutableListOf()
        }
    }
}