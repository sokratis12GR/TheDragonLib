package net.thedragonteam.thedragonlib.blocks

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.server.management.PlayerChunkMap
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.ReflectionHelper
import net.thedragonteam.thedragonlib.TheDragonLib
import net.thedragonteam.thedragonlib.network.PacketSyncableObject
import net.thedragonteam.thedragonlib.network.PacketTileMessage
import net.thedragonteam.thedragonlib.util.LogHelper
import net.thedragonteam.thedragonlib.wrappers.SyncableObject
import java.util.*

class TileTDLBase : TileEntity() {

    private val syncableObjectMap = HashMap<Byte, SyncableObject>()
    private var objIndexCount = 0
    private var viewRange = -1
    private var shouldRefreshOnState = true

    @JvmOverloads fun detectAndSendChanges(forceSync: Boolean = false) {
        if (world.isRemote) return
        syncableObjectMap.values
                .asSequence()
                .filter { it.syncInTile }
                .forEach { it.detectAndSendChanges(this, null, forceSync) }
    }

    fun detectAndSendChangesToPlayer(forceSync: Boolean, playerMP: EntityPlayerMP) {
        if (world.isRemote) return
        syncableObjectMap.values
                .asSequence()
                .filter { it.syncInContainer }
                .forEach { it.detectAndSendChanges(this, playerMP, forceSync) }
    }

    fun registerSyncableObject(`object`: SyncableObject, saveToNBT: Boolean) {
        assert(objIndexCount <= java.lang.Byte.MAX_VALUE) { "TileDCBase#registerSyncableObject To many objects registered!" }
        syncableObjectMap.put(objIndexCount.toByte(), `object`.setIndex(objIndexCount))
        if (saveToNBT) {
            `object`.setSaved()
        }
        objIndexCount++
    }

    fun receiveSyncPacketFromServer(packet: PacketSyncableObject) {
        if (syncableObjectMap.containsKey(packet.index)) {
            val `object` = syncableObjectMap[packet.index]
            `object`?.updateReceived(packet)

            if (`object`?.updateOnReceived!!) {
                updateBlock()
            }
        }
    }

    fun syncRange(): NetworkRegistry.TargetPoint {
        when {
            viewRange == -1 && !world.isRemote -> {
                val f = ReflectionHelper.findField(PlayerChunkMap::class.java, "playerViewRadius", "field_72698_e")
                f.isAccessible = true
                try {
                    viewRange = f.getInt((world as WorldServer).playerChunkMap)
                } catch (e: IllegalAccessException) {
                    LogHelper.error("A THING BROKE!!!!!!!")
                    e.printStackTrace()
                }

            }
            world.isRemote -> LogHelper.error("Hay! Someone is doing a bad thing!!! Check your side!!!!!!!")
        }
        return NetworkRegistry.TargetPoint(world.provider.dimension, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), (viewRange * 16).toDouble())
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        val nbttagcompound = NBTTagCompound()

        syncableObjectMap.values.forEach { syncableObject -> syncableObject.toNBT(nbttagcompound) }

        return SPacketUpdateTileEntity(this.pos, 0, nbttagcompound)
    }

    override fun getUpdateTag(): NBTTagCompound {
        val compound = super.getUpdateTag()

        syncableObjectMap.values
                .asSequence()
                .filter { it.shouldSave }
                .forEach { it.toNBT(compound) }

        writeExtraNBT(compound)

        return compound
    }

    override fun handleUpdateTag(tag: NBTTagCompound) {
        super.handleUpdateTag(tag)//todo?
    }

    override fun onDataPacket(net: NetworkManager?, pkt: SPacketUpdateTileEntity?) {
        syncableObjectMap.values.forEach { syncableObject -> syncableObject.fromNBT(pkt!!.nbtCompound) }
    }

    //endregion

    //region Packets

    /**
     * Send a message to the server side tile
     */
    fun sendPacketToServer(packet: PacketTileMessage) {
        TheDragonLib.network.sendToServer(packet)
    }

    /**
     * Receive a message from the client side tile. Override this to receive messages.
     */
    fun receivePacketFromClient(packet: PacketTileMessage, client: EntityPlayerMP) {

    }

    //endregion

    //region Helper Functions.

    fun updateBlock() {
        val state = world.getBlockState(getPos())
        world.notifyBlockUpdate(getPos(), state, state, 3)
    }


    fun dirtyBlock() {
        val chunk = world.getChunkFromBlockCoords(getPos())
        chunk.setChunkModified()
    }

    /**
     * Calling this in the constructor will force the tile to only refresh when the block changes rather then when the state changes.
     * Note that this should NOT be used in cases where the block has a different tile depending on its state.
     */
    fun setShouldRefreshOnBlockChange() {
        shouldRefreshOnState = false
    }
    //endregion

    /**
     * Write any extra data that needs to be saved to NBT that is not saved via a syncable field
     */
    fun writeExtraNBT(compound: NBTTagCompound) {
    }

    fun readExtraNBT(compound: NBTTagCompound) {
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)

        syncableObjectMap.values
                .asSequence()
                .filter { it.shouldSave }
                .forEach { it.toNBT(compound) }

        writeExtraNBT(compound)

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        syncableObjectMap.values
                .asSequence()
                .filter { it.shouldSave }
                .forEach { it.fromNBT(compound) }

        readExtraNBT(compound)
    }

    override fun shouldRefresh(world: World?, pos: BlockPos?, oldState: IBlockState, newSate: IBlockState): Boolean {
        return if (shouldRefreshOnState) oldState !== newSate else oldState.block !== newSate.block
    }
}//region Sync