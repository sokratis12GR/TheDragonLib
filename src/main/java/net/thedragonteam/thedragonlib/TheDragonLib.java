package net.thedragonteam.thedragonlib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
    public static final int MAJOR = 3;
    // Updates every time a new block, item or features is added or change,
    // resets on MAJOR changes
    public static final int MINOR = 0;
    // Updates every time a bug is fixed or issue solved or very minor code changes,
    // resets on MINOR changes
    public static final int PATCH = 0;
    // The TheDragonCore Version
    public static final String VERSION =
            TheDragonLib.MCVERSION + "-" + TheDragonLib.MAJOR + "." + TheDragonLib.MINOR + "." + TheDragonLib.PATCH;
    public static final String UPDATE_JSON = "http://fdn.redstone.tech/TheDragonTeam/thedragonlib/update.json";
    public static final String MODNAME = "TheDragonLib";
    public static final String MODID = "thedragonlib";
    public static final String CLIENT_SIDE = "net.thedragonteam.thedragonlib.proxy.ClientProxy";
    public static final String SERVER_SIDE = "net.thedragonteam.thedragonlib.proxy.ServerProxy";

    @Instance(TheDragonLib.MODID)
    public static TheDragonLib instance;

    @SidedProxy(clientSide = TheDragonLib.CLIENT_SIDE, serverSide = TheDragonLib.SERVER_SIDE)
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper network;

    public TheDragonLib() {
        LogHelper.INSTANCE.info("Welcoming Minecraft");
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FileHandler.INSTANCE.init(event);
        registerNetwork();
        proxy.preInit(event);
    }

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void initClient(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @SideOnly(Side.SERVER)
    @EventHandler
    public void initServer(FMLInitializationEvent event) {
        proxy.init(event);
    }


    public void registerNetwork() {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("TheDragonLibNet");
        network.registerMessage(PacketSyncableObject.Handler.class, PacketSyncableObject.class, 0, Side.CLIENT);
        network.registerMessage(PacketTileMessage.Handler.class, PacketTileMessage.class, 1, Side.SERVER);
    }
}