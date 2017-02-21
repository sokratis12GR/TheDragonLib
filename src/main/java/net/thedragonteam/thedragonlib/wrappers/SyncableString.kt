package net.thedragonteam.thedragonlib.wrappers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.thedragonteam.thedragonlib.TheDragonLib
import net.thedragonteam.thedragonlib.blocks.TileTDLBase
import net.thedragonteam.thedragonlib.network.PacketSyncableObject
import net.thedragonteam.thedragonlib.util.LogHelper

class SyncableString : SyncableObject {

    var value: String
    private var lastTickValue: String?
        get() = null

    constructor(value: String, syncInTile: Boolean, syncInContainer: Boolean, updateOnReceived: Boolean) : super(syncInTile, syncInContainer, updateOnReceived) {
        this.lastTickValue = value
        this.value = this.lastTickValue as String
    }

    constructor(value: String, syncInTile: Boolean, syncInContainer: Boolean) : super(syncInTile, syncInContainer) {
        this.lastTickValue = value
        this.value = this.lastTickValue as String
    }

    override fun detectAndSendChanges(tile: TileTDLBase, player: EntityPlayer?, forceSync: Boolean) {
        if (lastTickValue != value || forceSync) {
            lastTickValue = value
            tile.dirtyBlock()
            when (player) {
                null -> TheDragonLib.network.sendToAllAround(PacketSyncableObject(tile, index, value, updateOnReceived), tile.syncRange())
                is EntityPlayerMP -> TheDragonLib.network.sendTo(PacketSyncableObject(tile, index, value, updateOnReceived), player as EntityPlayerMP?)
                else -> LogHelper.error("SyncableInt#detectAndSendChanges No valid destination for sync packet!")
            }
        }
    }

    override fun updateReceived(packet: PacketSyncableObject) {
        if (packet.dataType == PacketSyncableObject.STRING_INDEX) {
            value = packet.stringValue
        }
    }

    override fun toNBT(compound: NBTTagCompound) {
        compound.setString("SyncableString" + index, value)
    }

    override fun fromNBT(compound: NBTTagCompound) {
        if (compound.hasKey("SyncableString" + index)) {
            value = compound.getString("SyncableString" + index)
        }
    }

    override fun toString(): String {
        return value
    }
}
