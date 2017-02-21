package net.thedragonteam.thedragonlib.handlers

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

import java.io.File

object FileHandler {
    lateinit var configFolder: File
    lateinit var mcDirectory: File

    fun init(event: FMLPreInitializationEvent) {
        configFolder = event.modConfigurationDirectory
        mcDirectory = configFolder.parentFile
    }
}