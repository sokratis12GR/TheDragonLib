package net.thedragonteam.thedragonlib.wrappers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagInt
import net.minecraft.nbt.NBTTagList
import net.thedragonteam.thedragonlib.TheDragonLib
import net.thedragonteam.thedragonlib.blocks.TileTDLBase
import net.thedragonteam.thedragonlib.lib.Vec3I
import net.thedragonteam.thedragonlib.network.PacketSyncableObject
import net.thedragonteam.thedragonlib.util.LogHelper

class SyncableVec3I : SyncableObject {

    var vec: Vec3I
    private var lastTickVec: Vec3I? = null


    constructor(vec: Vec3I, syncInTile: Boolean, syncInContainer: Boolean, updateOnReceived: Boolean) : super(syncInTile, syncInContainer, updateOnReceived) {
        this.vec = vec
        this.lastTickVec = vec
    }

    constructor(vec: Vec3I, syncInTile: Boolean, syncInContainer: Boolean) : super(syncInTile, syncInContainer) {
        this.vec = vec
        this.lastTickVec = vec
    }

    override fun detectAndSendChanges(tile: TileTDLBase, player: EntityPlayer?, forceSync: Boolean) {
        if (vec != lastTickVec || forceSync) {
            lastTickVec = vec.copy()
            tile.dirtyBlock()
            when (player) {
                null -> TheDragonLib.network.sendToAllAround(PacketSyncableObject(tile, index, vec, updateOnReceived), tile.syncRange())
                is EntityPlayerMP -> TheDragonLib.network.sendTo(PacketSyncableObject(tile, index, vec, updateOnReceived), player as EntityPlayerMP?)
                else -> LogHelper.error("SyncableInt#detectAndSendChanges No valid destination for sync packet!")
            }
        }
    }

    override fun updateReceived(packet: PacketSyncableObject) {
        if (packet.dataType == PacketSyncableObject.VEC3I_INDEX) {
            vec = packet.vec3I.copy()
        }
    }

    override fun toNBT(compound: NBTTagCompound) {
        val list = NBTTagList()
        list.appendTag(NBTTagInt(vec.x))
        list.appendTag(NBTTagInt(vec.y))
        list.appendTag(NBTTagInt(vec.z))

        compound.setTag("SyncableVec3I" + index, list)
    }

    override fun fromNBT(compound: NBTTagCompound) {
        if (compound.hasKey("SyncableVec3I" + index)) {
            val list = compound.getTagList("SyncableVec3I" + index, 3)
            if (list.tagCount() == 3) {
                vec[list.getIntAt(0), list.getIntAt(1)] = list.getIntAt(2)
            }
        }
    }

    override fun toString(): String {
        return vec.toString()
    }
}
