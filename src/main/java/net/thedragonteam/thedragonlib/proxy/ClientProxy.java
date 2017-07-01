package net.thedragonteam.thedragonlib.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public boolean isDedicatedServer() {
        return false;
    }

    @Override
    public MinecraftServer getMCServer() {
        return super.getMCServer();
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public boolean isJumpKeyDown() {
        return Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
    }

    @Override
    public boolean isSneakKeyDown() {
        return Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown();
    }

    @Override
    public boolean isSprintKeyDown() {
        return Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown();
    }

    @Override
    public EntityPlayerSP getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }
}