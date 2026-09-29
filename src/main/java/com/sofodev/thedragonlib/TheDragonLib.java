package com.sofodev.thedragonlib;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TheDragonLib.MODID)
public class TheDragonLib {

    public static final String MODID = "thedragonlib";

    public TheDragonLib(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        modBus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }
}