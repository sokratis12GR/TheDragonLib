package net.thedragonteam.thedragonlib.proxy

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.server.MinecraftServer
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.thedragonteam.thedragonlib.credits.TDLAchievements

open class CommonProxy {

    open fun preInit(event: FMLPreInitializationEvent) {
    }

    open fun init(event: FMLInitializationEvent) {
        TDLAchievements.init()
    }

    open val isDedicatedServer: Boolean
        get() = true

    open val mcServer: MinecraftServer
        get() = FMLCommonHandler.instance().minecraftServerInstance

    open val clientWorld: World?
        get() = null

    fun isOp(paramString: String): Boolean {
        var paramString = paramString
        val localMinecraftServer = FMLCommonHandler.instance().minecraftServerInstance
        paramString = paramString.trim { it <= ' ' }
        return localMinecraftServer.playerList.oppedPlayerNames.any { paramString.equals(it, ignoreCase = true) }
    }

    open val isJumpKeyDown: Boolean
        get() = false

    open val isSprintKeyDown: Boolean
        get() = false

    open val isSneakKeyDown: Boolean
        get() = false

    open val clientPlayer: EntityPlayerSP?
        get() = null

    fun registerEvents() {
        //Register to receive subscribed events
        MinecraftForge.EVENT_BUS.register(this)
        TDLAchievements.init()
    }
}