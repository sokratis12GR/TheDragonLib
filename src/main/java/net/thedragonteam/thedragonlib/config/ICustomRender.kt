package net.thedragonteam.thedragonlib.config

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

interface ICustomRender {
    /**
     * Use this to register a custom renderer for the block.
     */
    @SideOnly(Side.CLIENT)
    fun registerRenderer(feature: Feature)

    /**
     * Return true if the normal json model should still be registered for the item
     */
    fun registerNormal(feature: Feature): Boolean
}