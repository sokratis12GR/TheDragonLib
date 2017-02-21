package net.thedragonteam.thedragonlib.proxy

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.server.MinecraftServer
import net.minecraft.world.World
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

class ClientProxy : CommonProxy() {

    override fun preInit(event: FMLPreInitializationEvent) {
        super.preInit(event)
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)
    }

    override val isDedicatedServer: Boolean
        get() {
            return false
        }

    override val mcServer: MinecraftServer
        get() {
            return super.mcServer
        }

    override val clientWorld: World
        get() {
            return Minecraft.getMinecraft().world
        }

    override val isJumpKeyDown: Boolean
        get() {
            return Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown
        }

    override val isSneakKeyDown: Boolean
        get() {
            return Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown
        }

    override val isSprintKeyDown: Boolean
        get() {
            return Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown
        }

    override val clientPlayer: EntityPlayerSP
        get() {
            return Minecraft.getMinecraft().player
        }
}