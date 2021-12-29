package com.sofodev.thedragonlib;

import com.sofodev.thedragonlib.util.LogHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.sofodev.thedragonlib.TheDragonLib.MODID;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("thedragonlib")
@Mod.EventBusSubscriber(bus = MOD, modid = MODID)
public class TheDragonLib {

    public static final String MODID = "thedragonlib";

    public TheDragonLib() {
        LogHelper.info("Welcoming Minecraft");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
        //
    }
}
