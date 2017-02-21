package net.thedragonteam.thedragonlib.wrappers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.thedragonteam.thedragonlib.TheDragonLib
import net.thedragonteam.thedragonlib.blocks.TileTDLBase
import net.thedragonteam.thedragonlib.network.PacketSyncableObject

class SyncableBool : SyncableObject {

    var value: Boolean = false
    var lastTickValue: Boolean = false

    constructor(value: Boolean, syncInTile: Boolean, syncInContainer: Boolean, updateOnReceived: Boolean) : super(syncInTile, syncInContainer, updateOnReceived) {
        this.lastTickValue = value
        this.value = this.lastTickValue
    }

    constructor(value: Boolean, syncInTile: Boolean, syncInContainer: Boolean) : super(syncInTile, syncInContainer) {
        this.lastTickValue = value
        this.value = this.lastTickValue
    }

    override fun detectAndSendChanges(tile: TileTDLBase, player: EntityPlayer?, forceSync: Boolean) {
        if (lastTickValue != value || forceSync) {
            lastTickValue = value
            tile.dirtyBlock()
            when (player) {
                null -> TheDragonLib.network.sendToAllAround(PacketSyncableObject(tile, index, value, updateOnReceived), tile.syncRange())
                is EntityPlayerMP -> TheDragonLib.network.sendTo(PacketSyncableObject(tile, index, value, updateOnReceived), player)
            }
        }
    }

    override fun updateReceived(packet: PacketSyncableObject) {
        if (packet.dataType == PacketSyncableObject.BOOLEAN_INDEX) {
            value = packet.booleanValue
        }
    }

    override fun toNBT(compound: NBTTagCompound) {
        compound.setBoolean("SyncableBool" + index, value)
    }

    override fun fromNBT(compound: NBTTagCompound) {
        if (compound.hasKey("SyncableBool" + index)) {
            value = compound.getBoolean("SyncableBool" + index)
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}
