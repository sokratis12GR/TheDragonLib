package net.thedragonteam.thedragonlib.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.thedragonteam.thedragonlib.blocks.TileTDLBase;
import net.thedragonteam.thedragonlib.network.PacketSyncableObject;

public abstract class SyncableObject {
    public final boolean syncInTile;
    public final boolean syncInContainer;
    public boolean shouldSave = false;
    public boolean updateOnReceived;
    protected byte index = -1;

    public SyncableObject(boolean syncInTile, boolean syncInContainer) {
        this(syncInTile, syncInContainer, false);
    }

    public SyncableObject(boolean syncInTile, boolean syncInContainer, boolean updateOnReceived) {
        this.syncInTile = syncInTile;
        this.syncInContainer = syncInContainer;
        this.updateOnReceived = updateOnReceived;
    }

    public abstract void detectAndSendChanges(TileTDLBase tile, EntityPlayer player, boolean forceSync);

    public abstract void updateReceived(PacketSyncableObject packet);

    public SyncableObject setIndex(int index) {
        this.index = (byte) index;
        return this;
    }

    public abstract void toNBT(NBTTagCompound compound);

    public abstract void fromNBT(NBTTagCompound compound);

    public SyncableObject setSaved() {
        shouldSave = true;
        return this;
    }
}
