package net.thedragonteam.thedragonlib.wrappers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.thedragonteam.thedragonlib.TheDragonLib
import net.thedragonteam.thedragonlib.blocks.TileTDLBase
import net.thedragonteam.thedragonlib.network.PacketSyncableObject
import net.thedragonteam.thedragonlib.util.LogHelper

class SyncableInt : SyncableObject {

    var value: Int = 0
    private var lastTickValue: Int = 0

    constructor(value: Int, syncInTile: Boolean, syncInContainer: Boolean, updateOnReceived: Boolean) : super(syncInTile, syncInContainer, updateOnReceived) {
        this.lastTickValue = value
        this.value = this.lastTickValue
    }

    constructor(value: Int, syncInTile: Boolean, syncInContainer: Boolean) : super(syncInTile, syncInContainer) {
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
                else -> LogHelper.error("SyncableInt#detectAndSendChanges No valid destination for sync packet!")
            }
        }
    }

    override fun updateReceived(packet: PacketSyncableObject) {
        if (packet.dataType == PacketSyncableObject.INT_INDEX) {
            value = packet.intValue
        }
    }

    override fun toNBT(compound: NBTTagCompound) {
        compound.setInteger("SyncableInt" + index, value)
    }

    override fun fromNBT(compound: NBTTagCompound) {
        if (compound.hasKey("SyncableInt" + index)) {
            value = compound.getInteger("SyncableInt" + index)
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}
