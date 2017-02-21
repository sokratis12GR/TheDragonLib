package net.thedragonteam.thedragonlib.wrappers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.thedragonteam.thedragonlib.blocks.TileTDLBase
import net.thedragonteam.thedragonlib.network.PacketSyncableObject

abstract class SyncableObject @JvmOverloads constructor(val syncInTile: Boolean, val syncInContainer: Boolean, var updateOnReceived: Boolean = false) {
    var shouldSave = false
    protected var index: Byte = -1

    abstract fun detectAndSendChanges(tile: TileTDLBase, player: EntityPlayer?, forceSync: Boolean)

    abstract fun updateReceived(packet: PacketSyncableObject)

    fun setIndex(index: Int): SyncableObject {
        this.index = index.toByte()
        return this
    }

    abstract fun toNBT(compound: NBTTagCompound)

    abstract fun fromNBT(compound: NBTTagCompound)

    fun setSaved(): SyncableObject {
        shouldSave = true
        return this
    }
}
