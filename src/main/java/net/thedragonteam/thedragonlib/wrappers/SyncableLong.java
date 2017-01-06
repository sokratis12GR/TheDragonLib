package net.thedragonteam.thedragonlib.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.thedragonteam.thedragonlib.TheDragonLib;
import net.thedragonteam.thedragonlib.blocks.TileTDLBase;
import net.thedragonteam.thedragonlib.network.PacketSyncableObject;
import net.thedragonteam.thedragonlib.util.LogHelper;

public class SyncableLong extends SyncableObject {

    public long value;
    public long lastTickValue;

    public SyncableLong(long value, boolean syncInTile, boolean syncInContainer, boolean updateOnReceived) {
        super(syncInTile, syncInContainer, updateOnReceived);
        this.value = this.lastTickValue = value;
    }

    public SyncableLong(long value, boolean syncInTile, boolean syncInContainer) {
        super(syncInTile, syncInContainer);
        this.value = this.lastTickValue = value;
    }

    @Override
    public void detectAndSendChanges(TileTDLBase tile, EntityPlayer player, boolean forceSync) {
        if (lastTickValue != value || forceSync) {
            lastTickValue = value;
            tile.dirtyBlock();
            if (player == null) {
                TheDragonLib.network.sendToAllAround(new PacketSyncableObject(tile, index, value, updateOnReceived), tile.syncRange());
            } else if (player instanceof EntityPlayerMP) {
                TheDragonLib.network.sendTo(new PacketSyncableObject(tile, index, value, updateOnReceived), (EntityPlayerMP) player);
            } else LogHelper.error("SyncableInt#detectAndSendChanges No valid destination for sync packet!");
        }
    }

    @Override
    public void updateReceived(PacketSyncableObject packet) {
        if (packet.dataType == PacketSyncableObject.LONG_INDEX) {
            value = packet.longValue;
        }
    }

    @Override
    public void toNBT(NBTTagCompound compound) {
        compound.setLong("SyncableLong" + index, value);
    }

    @Override
    public void fromNBT(NBTTagCompound compound) {
        if (compound.hasKey("SyncableLong" + index)) {
            value = compound.getLong("SyncableLong" + index);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
