package net.thedragonteam.thedragonlib.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.thedragonteam.thedragonlib.blocks.TileTDLBase;
import net.thedragonteam.thedragonlib.network.PacketSyncableObject;

public class SyncableNBT extends SyncableObject {

//	public NBTTagCompound value;
//	private NBTTagCompound lastTickValue;

    public SyncableNBT(NBTTagCompound value, boolean syncInTile, boolean syncInContainer) {
        super(syncInTile, syncInContainer);
        //	this.value = this.lastTickValue = value;
    }

    @Override
    public void detectAndSendChanges(TileTDLBase tile, EntityPlayer player, boolean forceSync) {
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

    @Override
    public void updateReceived(PacketSyncableObject packet) {
//		if (packet.dataType == PacketSyncableObject.TAG_INDEX){
//			value = packet.compound;
//		}
    }

    @Override
    public void toNBT(NBTTagCompound compound) {

    }

    @Override
    public void fromNBT(NBTTagCompound compound) {

    }


}
