package net.thedragonteam.thedragonlib.proxy;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.thedragonteam.thedragonlib.credits.TDLAchievements;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
    }

    public void init(FMLInitializationEvent event) {
        TDLAchievements.init();
    }

    public boolean isDedicatedServer() {
        return true;
    }

    public MinecraftServer getMCServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public World getClientWorld() {
        return null;
    }

    public boolean isOp(String paramString) {
        MinecraftServer localMinecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        paramString = paramString.trim();
        for (String str : localMinecraftServer.getPlayerList().getOppedPlayerNames()) {
            if (paramString.equalsIgnoreCase(str)) return true;
        }
        return false;
    }

    public boolean isJumpKeyDown() {
        return false;
    }

    public boolean isSprintKeyDown() {
        return false;
    }

    public boolean isSneakKeyDown() {
        return false;
    }

    public EntityPlayerSP getClientPlayer() {
        return null;
    }

    public void registerEvents() {
        //Register to receive subscribed events
        MinecraftForge.EVENT_BUS.register(this);
        TDLAchievements.init();
    }
}