package net.thedragonteam.thedragonlib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.thedragonteam.thedragonlib.handlers.FileHandler;
import net.thedragonteam.thedragonlib.network.PacketSyncableObject;
import net.thedragonteam.thedragonlib.network.PacketTileMessage;
import net.thedragonteam.thedragonlib.proxy.CommonProxy;
import net.thedragonteam.thedragonlib.util.LogHelper;

@Mod(modid = TheDragonLib.MODID, name = TheDragonLib.MODNAME, version = TheDragonLib.VERSION, updateJSON = TheDragonLib.UPDATE_JSON)
public class TheDragonLib {
    public static final String MCVERSION = "1.11.2";
    // Updates every MAJOR change,
    // never resets
    public static final int MAJOR = 2;
    // Updates every time a new block, item or features is added or change,
    // resets on MAJOR changes
    public static final int MINOR = 1;
    // Updates every time a bug is fixed or issue solved or very minor code changes,
    // resets on MINOR changes
    public static final int PATCH = 0;
    // The TheDragonCore Version
    public static final String VERSION =
            TheDragonLib.MCVERSION + "-" + TheDragonLib.MAJOR + "." + TheDragonLib.MINOR + "." + TheDragonLib.PATCH;
    public static final String UPDATE_JSON = "http://fdn.redstone.tech/TheDragonTeam/thedragonlib/update.json";
    public static final String MODNAME = "TheDragonLib";
    public static final String MODID = "thedragonlib";

    @Mod.Instance(TheDragonLib.MODID)
    public static TheDragonLib instance;

    @SidedProxy(clientSide = "net.thedragonteam.thedragonlib.proxy.ClientProxy", serverSide = "net.thedragonteam.thedragonlib.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper network;

    public TheDragonLib() {
        LogHelper.info("Welcoming Minecraft");
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FileHandler.init(event);
        registerNetwork();
        proxy.preInit(event);
    }

    public void registerNetwork() {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("TheDragonLibNet");
        network.registerMessage(PacketSyncableObject.Handler.class, PacketSyncableObject.class, 0, Side.CLIENT);
        network.registerMessage(PacketTileMessage.Handler.class, PacketTileMessage.class, 1, Side.SERVER);
    }
}