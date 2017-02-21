package net.thedragonteam.thedragonlib.config

import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Feature(
        /**
         * used For All
         */
        val name: String,
        /**
         * used For All
         */
        val isActive: Boolean = true,
        /**
         * used For All
         */
        val isConfigurable: Boolean = true,
        /**
         * used For All
         */
        val variantMap: Array<String> = arrayOf(),
        /**
         * used For Items with a single variant
         */
        val stateOverride: String = "",
        /**
         * used For Blocks
         */
        val itemBlock: KClass<out ItemBlock> = ItemBlock::class,
        /**
         * used For Blocks
         */
        val tileEntity: KClass<out TileEntity> = TileEntity::class,
        /**
         * used For All
         * set to -1 to exclude from creative tab.
         */
        val cTab: Int = 0)