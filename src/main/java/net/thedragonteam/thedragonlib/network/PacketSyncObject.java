package net.thedragonteam.thedragonlib.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.thedragonteam.thedragonlib.util.LogHelper;

public abstract class PacketSyncObject<REQ extends IMessage, REPLY extends IMessage> implements Runnable {

    public final REQ message;
    public final MessageContext ctx;
    public REPLY reply;

    public PacketSyncObject(REQ message, MessageContext ctx) {
        this.message = message;
        this.ctx = ctx;
    }

    @Override
    public abstract void run();

    public void addPacketServer() {
        switch (ctx.side) {
            case CLIENT:
                LogHelper.error("[SyncPacket#addPacketServer] HEY!!! I caught you this time! WRONG SIDE!!!! - " + message.getClass());
                return;
        }
        ctx.getServerHandler().player.getServer().addScheduledTask(this);
    }

    public void addPacketClient() {
        switch (ctx.side) {
            case SERVER:
                LogHelper.error("[SyncPacket#addPacketClient] HEY!!! I caught you this time! WRONG SIDE!!!! - " + message.getClass());
                return;
        }
        Minecraft.getMinecraft().addScheduledTask(this);
    }
}