package net.thedragonteam.thedragonlib.client

import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.thedragonteam.thedragonlib.TheDragonLib
import java.util.*

object ResourceHelperDL {

    val RESOURCE_PREFIX = TheDragonLib.MODID + ":"
    private val cachedResources = HashMap<String, ResourceLocation>()

    fun bindTexture(texture: ResourceLocation) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture)
    }

    fun getResource(rs: String): ResourceLocation {
        if (!cachedResources.containsKey(rs)) cachedResources.put(rs, ResourceLocation(RESOURCE_PREFIX + rs))
        return cachedResources[rs] as ResourceLocation
    }

    fun getResourceRAW(rs: String): ResourceLocation {
        if (!cachedResources.containsKey(rs)) cachedResources.put(rs, ResourceLocation(rs))
        return cachedResources[rs] as ResourceLocation
    }

    fun bindTexture(rs: String) {
        bindTexture(getResource(rs))
    }
}