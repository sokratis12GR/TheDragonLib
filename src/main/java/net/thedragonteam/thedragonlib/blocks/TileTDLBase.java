package net.thedragonteam.thedragonlib.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.thedragonteam.thedragonlib.TheDragonLib;
import net.thedragonteam.thedragonlib.api.IDataRetainerTile;
import net.thedragonteam.thedragonlib.network.PacketSyncableObject;
import net.thedragonteam.thedragonlib.network.PacketTileMessage;
import net.thedragonteam.thedragonlib.util.LogHelper;
import net.thedragonteam.thedragonlib.wrappers.SyncableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TileTDLBase extends TileEntity {

    private Map<Byte, SyncableObject> syncableObjectMap = new HashMap<>();
    private int objIndexCount = 0;
    private int viewRange = -1;
    private boolean shouldRefreshOnState = true;

    //region Sync
    public void detectAndSendChanges() {
        detectAndSendChanges(false);
    }

    public void detectAndSendChanges(boolean forceSync) {
        if (world.isRemote) return;
        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            if (syncableObject.syncInTile) {
                syncableObject.detectAndSendChanges(this, null, forceSync);
            }
        }
    }

    public void detectAndSendChangesToPlayer(boolean forceSync, EntityPlayerMP playerMP) {
        if (world.isRemote) return;
        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            if (syncableObject.syncInContainer) {
                syncableObject.detectAndSendChanges(this, playerMP, forceSync);
            }
        }
    }

    public void registerSyncableObject(SyncableObject object, boolean saveToNBT) {
        assert objIndexCount <= Byte.MAX_VALUE : "TileDCBase#registerSyncableObject To many objects registered!";
        syncableObjectMap.put((byte) objIndexCount, object.setIndex(objIndexCount));
        if (saveToNBT) {
            object.setSaved();
        }
        objIndexCount++;
    }

    public void receiveSyncPacketFromServer(PacketSyncableObject packet) {
        if (syncableObjectMap.containsKey(packet.index)) {
            SyncableObject object = syncableObjectMap.get(packet.index);
            object.updateReceived(packet);

            if (object.updateOnReceived) {
                updateBlock();
            }
        }
    }

    public NetworkRegistry.TargetPoint syncRange() {
        if (viewRange == -1 && !world.isRemote) {
            Field f = ReflectionHelper.findField(PlayerChunkMap.class, "playerViewRadius", "field_72698_e");
            f.setAccessible(true);
            try {
                viewRange = f.getInt(((WorldServer) world).getPlayerChunkMap());
            } catch (IllegalAccessException e) {
                LogHelper.error("A THING BROKE!!!!!!!");
                e.printStackTrace();
            }
        } else if (world.isRemote) {
            LogHelper.error("Hay! Someone is doing a bad thing!!! Check your side!!!!!!!");
        }
        return new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), viewRange * 16);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (this instanceof IDataRetainerTile) {
            ((IDataRetainerTile) this).writeDataToNBT(nbttagcompound);
        }

        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            syncableObject.toNBT(nbttagcompound);
        }

        return new SPacketUpdateTileEntity(this.pos, 0, nbttagcompound);
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = super.getUpdateTag();

        if (this instanceof IDataRetainerTile) {
            ((IDataRetainerTile) this).writeDataToNBT(compound);
        }

        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            if (syncableObject.shouldSave) {
                syncableObject.toNBT(compound);
            }
        }

        writeExtraNBT(compound);

        return compound;
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        super.handleUpdateTag(tag);//todo?
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        if (this instanceof IDataRetainerTile) {
            //noinspection MethodCallSideOnly
            ((IDataRetainerTile) this).readDataFromNBT(pkt.getNbtCompound());
        }

        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            //noinspection MethodCallSideOnly
            syncableObject.fromNBT(pkt.getNbtCompound());
        }
    }

    //endregion

    //region Packets

    /**
     * Send a message to the server side tile
     */
    public void sendPacketToServer(PacketTileMessage packet) {
        TheDragonLib.network.sendToServer(packet);
    }

    /**
     * Receive a message from the client side tile. Override this to receive messages.
     */
    public void receivePacketFromClient(PacketTileMessage packet, EntityPlayerMP client) {

    }

    //endregion

    //region Helper Functions.

    public void updateBlock() {
        IBlockState state = world.getBlockState(getPos());
        world.notifyBlockUpdate(getPos(), state, state, 3);
    }


    public void dirtyBlock() {
        Chunk chunk = world.getChunkFromBlockCoords(getPos());
        chunk.markDirty();
    }

    /**
     * Calling this in the constructor will force the tile to only refresh when the block changes rather then when the state changes.
     * Note that this should NOT be used in cases where the block has a different tile depending on its state.
     */
    public void setShouldRefreshOnBlockChange() {
        shouldRefreshOnState = false;
    }
    //endregion

    /**
     * Write any extra data that needs to be saved to NBT that is not saved via a syncable field
     */
    public void writeExtraNBT(NBTTagCompound compound) {
    }

    public void readExtraNBT(NBTTagCompound compound) {
    }

    @Nonnull
    @Override
    public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (this instanceof IDataRetainerTile) {
            ((IDataRetainerTile) this).writeDataToNBT(compound);
        }

        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            if (syncableObject.shouldSave) {
                syncableObject.toNBT(compound);
            }
        }

        writeExtraNBT(compound);

        return compound;
    }

    @Override
    public final void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (this instanceof IDataRetainerTile) {
            ((IDataRetainerTile) this).readDataFromNBT(compound);
        }

        for (SyncableObject syncableObject : syncableObjectMap.values()) {
            if (syncableObject.shouldSave) {
                syncableObject.fromNBT(compound);
            }
        }

        readExtraNBT(compound);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newSate) {
        return shouldRefreshOnState ? oldState != newSate : (oldState.getBlock() != newSate.getBlock());
    }
}