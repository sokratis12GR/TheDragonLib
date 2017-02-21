package net.thedragonteam.thedragonlib.network

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import net.thedragonteam.thedragonlib.blocks.TileTDLBase

class PacketTileMessage : IMessage {

    lateinit var tilePos: BlockPos
    var stringValue = ""
    var floatValue = 0f
    var doubleValue = 0.0
    var intValue = 0
    var byteValue: Byte = 0
    var booleanValue = false
    lateinit var compound: NBTTagCompound
    var dataType: Byte = 0
    var index: Byte = 0
        private set

    constructor() {
    }

    constructor(tile: TileTDLBase, pktIndex: Byte, booleanValue: Boolean, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = pktIndex
        this.booleanValue = booleanValue
        this.dataType = BOOLEAN_INDEX
    }

    constructor(tile: TileTDLBase, pktIndex: Byte, byteValue: Byte, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = pktIndex
        this.byteValue = byteValue
        this.dataType = BYTE_INDEX
    }

    constructor(tile: TileTDLBase, pktIndex: Byte, intValue: Int, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = pktIndex
        this.intValue = intValue
        this.dataType = INT_INDEX
    }

    constructor(tile: TileTDLBase, pktIndex: Byte, doubleValue: Double, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = pktIndex
        this.doubleValue = doubleValue
        this.dataType = DOUBLE_INDEX
    }

    constructor(tile: TileTDLBase, pktIndex: Byte, floatValue: Float, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = pktIndex
        this.floatValue = floatValue
        this.dataType = FLOAT_INDEX
    }

    constructor(tile: TileTDLBase, pktIndex: Byte, stringValue: String, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = pktIndex
        this.stringValue = stringValue
        this.dataType = STRING_INDEX
    }

    constructor(tile: TileTDLBase, pktIndex: Byte, compound: NBTTagCompound, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = pktIndex
        this.compound = compound
        this.dataType = TAG_INDEX
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeByte(dataType.toInt())
        buf.writeByte(index.toInt())

        buf.writeInt(tilePos.x)
        buf.writeInt(tilePos.y)
        buf.writeInt(tilePos.z)


        when (dataType) {
            BOOLEAN_INDEX -> buf.writeBoolean(booleanValue)
            BYTE_INDEX -> buf.writeByte(byteValue.toInt())
            INT_INDEX -> buf.writeInt(intValue)
            DOUBLE_INDEX -> buf.writeDouble(doubleValue)
            FLOAT_INDEX -> buf.writeFloat(floatValue)
            STRING_INDEX -> ByteBufUtils.writeUTF8String(buf, stringValue)
            TAG_INDEX -> ByteBufUtils.writeTag(buf, compound)
        }
    }

    override fun fromBytes(buf: ByteBuf) {
        dataType = buf.readByte()
        index = buf.readByte()

        val x = buf.readInt()
        val y = buf.readInt()
        val z = buf.readInt()
        tilePos = BlockPos(x, y, z)


        when (dataType) {
            BOOLEAN_INDEX -> booleanValue = buf.readBoolean()
            BYTE_INDEX -> byteValue = buf.readByte()
            INT_INDEX -> intValue = buf.readInt()
            DOUBLE_INDEX -> doubleValue = buf.readDouble()
            FLOAT_INDEX -> floatValue = buf.readFloat()
            STRING_INDEX -> stringValue = ByteBufUtils.readUTF8String(buf)
            TAG_INDEX -> compound = ByteBufUtils.readTag(buf) as NBTTagCompound
        }
    }

    val isBool: Boolean
        get() = dataType == BOOLEAN_INDEX

    val isByte: Boolean
        get() = dataType == BYTE_INDEX

    val isInt: Boolean
        get() = dataType == INT_INDEX

    val isDouble: Boolean
        get() = dataType == DOUBLE_INDEX

    val isFload: Boolean
        get() = dataType == FLOAT_INDEX

    val isString: Boolean
        get() = dataType == STRING_INDEX

    val isNBT: Boolean
        get() = dataType == TAG_INDEX

    class Handler : MessageHandlerWrapper<PacketTileMessage, IMessage>() {

        override fun handleMessage(message: PacketTileMessage, ctx: MessageContext): IMessage? {
            if (ctx.side == Side.SERVER) {
                val syncPacket = object : PacketSyncObject<PacketTileMessage, IMessage>(message, ctx) {
                    override fun run() {
                        val tile = ctx.serverHandler.player.world.getTileEntity(message.tilePos)
                        if (tile is TileTDLBase) {
                            tile.receivePacketFromClient(message, ctx.serverHandler.player)
                        }
                    }
                }

                syncPacket.addPacketServer()
            }
            return null
        }
    }

    companion object {
        val BOOLEAN_INDEX: Byte = 0
        val BYTE_INDEX: Byte = 1
        val INT_INDEX: Byte = 2
        val DOUBLE_INDEX: Byte = 3
        val FLOAT_INDEX: Byte = 4
        val STRING_INDEX: Byte = 5
        val TAG_INDEX: Byte = 6
    }
}