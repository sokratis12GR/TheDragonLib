package net.thedragonteam.thedragonlib.network

import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side

abstract class MessageHandlerWrapper<REQ : IMessage, REPLY : IMessage> : IMessageHandler<REQ, REPLY> {

    override fun onMessage(message: REQ, ctx: MessageContext): REPLY {

        val syncObject = object : PacketSyncObject<REQ, REPLY>(message, ctx) {
            override fun run() {
                reply = handleMessage(message, ctx)
            }
        }

        when (ctx.side) {
            Side.CLIENT -> syncObject.addPacketClient()
            else -> syncObject.addPacketServer()
        }

        return syncObject.reply as REPLY
    }

    abstract fun handleMessage(message: REQ, ctx: MessageContext): REPLY?

}