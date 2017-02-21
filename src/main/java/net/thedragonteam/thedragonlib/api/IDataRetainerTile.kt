package net.thedragonteam.thedragonlib.api

import net.minecraft.nbt.NBTTagCompound

/**
 * Implemented by the TileEntity of blocks that need to retain custom data when harvested.
 */
interface IDataRetainerTile {

    /**
     * Used to write custom tile specific data to NBT.
     * Data saved in this method will be synced with the client via description packets.
     * Data saved in this method will also be saved to the ItemBlock when the tile is harvested so it can be restored
     * when the tile is placed.
     */
    fun writeDataToNBT(dataCompound: NBTTagCompound)

    /**
     * This is where any data saved in writeDataToNBT should be loaded from NBT.
     */
    fun readDataFromNBT(dataCompound: NBTTagCompound)
}