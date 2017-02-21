package net.thedragonteam.thedragonlib.network

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import net.thedragonteam.thedragonlib.util.LogHelper

abstract class PacketSyncObject<out REQ : IMessage, REPLY : IMessage>(val message: REQ, val ctx: MessageContext) : Runnable {
    var reply: REPLY? = null

    abstract override fun run()

    fun addPacketServer() {
        if (ctx.side == Side.CLIENT) {
            LogHelper.error("[SyncPacket#addPacketServer] HEY!!! I caught you this time! WRONG SIDE!!!! - " + message.javaClass)
            return
        }
        ctx.serverHandler.player.server!!.addScheduledTask(this)
    }

    fun addPacketClient() {
        if (ctx.side == Side.SERVER) {
            LogHelper.error("[SyncPacket#addPacketClient] HEY!!! I caught you this time! WRONG SIDE!!!! - " + message.javaClass)
            return
        }
        Minecraft.getMinecraft().addScheduledTask(this)
    }
}