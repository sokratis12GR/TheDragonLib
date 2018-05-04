package net.thedragonteam.thedragonlib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.thedragonteam.thedragonlib.handlers.FileHandler;
import net.thedragonteam.thedragonlib.proxy.CommonProxy;
import net.thedragonteam.thedragonlib.util.LogHelper;

@Mod(modid = TheDragonLib.MODID,
        name = TheDragonLib.MODNAME,
        version = TDLVersion.VERSION,
        updateJSON = TheDragonLib.UPDATE_JSON,
        dependencies = "required-after:forge@[14.22.0.2459,);"
)
public class TheDragonLib {
    public static final String UPDATE_JSON = "https://download.nodecdn.net/containers/thedragonteam/thedragonlib-updater.json";
    public static final String MODNAME = "TheDragonLib";
    public static final String MODID = "thedragonlib";
    public static final String CLIENT_SIDE = "net.thedragonteam.thedragonlib.proxy.ClientProxy";
    public static final String SERVER_SIDE = "net.thedragonteam.thedragonlib.proxy.ServerProxy";

    @Instance(TheDragonLib.MODID)
    public static TheDragonLib instance;

    @SidedProxy(clientSide = TheDragonLib.CLIENT_SIDE, serverSide = TheDragonLib.SERVER_SIDE)
    public static CommonProxy proxy;

    public TheDragonLib() {
        LogHelper.info("Welcoming Minecraft");
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FileHandler.init(event);
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}