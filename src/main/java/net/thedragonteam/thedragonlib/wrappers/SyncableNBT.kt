package net.thedragonteam.thedragonlib.wrappers

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.thedragonteam.thedragonlib.blocks.TileTDLBase
import net.thedragonteam.thedragonlib.network.PacketSyncableObject

class SyncableNBT
//	public NBTTagCompound value;
//	private NBTTagCompound lastTickValue;

(value: NBTTagCompound, syncInTile: Boolean, syncInContainer: Boolean)//	this.value = this.lastTickValue = value;
    : SyncableObject(syncInTile, syncInContainer) {

    override fun detectAndSendChanges(tile: TileTDLBase, player: EntityPlayer?, forceSync: Boolean) {
        //If i implement this be sure to check each tag and only sent tags that have changed.
        //		if (!lastTickValue.equals(value)) {
        //			lastTickValue = value;
        //			if (tile != null) {
        //				BrandonsCore.network.sendToAllAround(new PacketSyncableObject(tile, index, value), tile.syncRange());
        //			}
        //			else if (player instanceof EntityPlayerMP) {
        //				BrandonsCore.network.sendTo(new PacketSyncableObject(null, index, value), (EntityPlayerMP)player);
        //			}
        //			else LogHelper.error("SyncableInt#detectAndSendChanges No valid destination for sync packet!");
        //		}
    }

    override fun updateReceived(packet: PacketSyncableObject) {
        //		if (packet.dataType == PacketSyncableObject.TAG_INDEX){
        //			value = packet.compound;
        //		}
    }

    override fun toNBT(compound: NBTTagCompound) {

    }

    override fun fromNBT(compound: NBTTagCompound) {

    }


}
