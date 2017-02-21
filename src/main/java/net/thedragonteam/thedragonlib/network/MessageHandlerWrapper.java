package net.thedragonteam.thedragonlib.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class MessageHandlerWrapper<REQ extends IMessage, REPLY extends IMessage> implements IMessageHandler<REQ, REPLY> {

    @Override
    public REPLY onMessage(REQ message, MessageContext ctx) {

        PacketSyncObject<REQ, REPLY> syncObject = new PacketSyncObject<REQ, REPLY>(message, ctx) {
            @Override
            public void run() {
                reply = handleMessage(message, ctx);
            }
        };

        switch (ctx.side) {
            case CLIENT:
                syncObject.addPacketClient();
                break;
            default:
                syncObject.addPacketServer();
                break;
        }

        return syncObject.reply;
    }

    public abstract REPLY handleMessage(REQ message, MessageContext ctx);

}