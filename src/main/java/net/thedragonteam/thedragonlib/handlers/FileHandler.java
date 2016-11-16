package net.thedragonteam.thedragonlib.handlers;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class FileHandler {
    public static File configFolder;
    public static File mcDirectory;

    public static void init(FMLPreInitializationEvent event) {
        configFolder = event.getModConfigurationDirectory();
        mcDirectory = configFolder.getParentFile();
    }
}