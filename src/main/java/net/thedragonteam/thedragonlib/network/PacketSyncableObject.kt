package net.thedragonteam.thedragonlib.network

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import net.thedragonteam.thedragonlib.blocks.TileTDLBase
import net.thedragonteam.thedragonlib.lib.Vec3I

class PacketSyncableObject : IMessage {


    lateinit var tilePos: BlockPos
    var index: Byte = 0
    var stringValue = ""
    var floatValue = 0f
    var doubleValue = 0.0
    var intValue = 0
    var shortValue: Short = 0
    var byteValue: Byte = 0
    var booleanValue = false
    lateinit var compound: NBTTagCompound
    lateinit var vec3I: Vec3I
    var longValue: Long = 0
    var updateOnReceived: Boolean = false
    var dataType: Byte = 0

    constructor() {
    }

    constructor(tile: TileTDLBase, syncableIndex: Byte, booleanValue: Boolean, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = syncableIndex
        this.booleanValue = booleanValue
        this.dataType = BOOLEAN_INDEX
    }

    constructor(tile: TileTDLBase, syncableIndex: Byte, byteValue: Byte, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = syncableIndex
        this.byteValue = byteValue
        this.dataType = BYTE_INDEX
    }

    constructor(tile: TileTDLBase, syncableIndex: Byte, shortValue: Short, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = syncableIndex
        this.shortValue = shortValue
        this.dataType = SHORT_INDEX
    }

    constructor(tile: TileTDLBase, syncableIndex: Byte, intValue: Int, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = syncableIndex
        this.intValue = intValue
        this.dataType = INT_INDEX
    }

    constructor(tile: TileTDLBase, syncableIndex: Byte, doubleValue: Double, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = syncableIndex
        this.doubleValue = doubleValue
        this.dataType = DOUBLE_INDEX
    }

    constructor(tile: TileTDLBase, syncableIndex: Byte, floatValue: Float, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = syncableIndex
        this.floatValue = floatValue
        this.dataType = FLOAT_INDEX
    }

    constructor(tile: TileTDLBase, syncableIndex: Byte, stringValue: String, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = syncableIndex
        this.stringValue = stringValue
        this.dataType = STRING_INDEX
    }

    constructor(tile: TileTDLBase, syncableIndex: Byte, compound: NBTTagCompound, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = syncableIndex
        this.compound = compound
        this.dataType = TAG_INDEX
    }

    constructor(tile: TileTDLBase, syncableIndex: Byte, vec3I: Vec3I, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = syncableIndex
        this.vec3I = vec3I
        this.dataType = VEC3I_INDEX
    }

    constructor(tile: TileTDLBase, syncableIndex: Byte, longValue: Long, updateOnReceived: Boolean) {
        this.tilePos = tile.pos
        this.index = syncableIndex
        this.longValue = longValue
        this.dataType = LONG_INDEX
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
            VEC3I_INDEX -> {
                buf.writeInt(vec3I.x)
                buf.writeInt(vec3I.y)
                buf.writeInt(vec3I.z)
            }
            LONG_INDEX -> buf.writeLong(longValue)
            SHORT_INDEX -> buf.writeShort(shortValue.toInt())
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
            VEC3I_INDEX -> {
                vec3I = Vec3I(0, 0, 0)
                vec3I.x = buf.readInt()
                vec3I.y = buf.readInt()
                vec3I.z = buf.readInt()
            }
            LONG_INDEX -> longValue = buf.readLong()
            SHORT_INDEX -> shortValue = buf.readShort()
        }
    }

    class Handler : MessageHandlerWrapper<PacketSyncableObject, IMessage>() {

        override fun handleMessage(message: PacketSyncableObject, ctx: MessageContext): IMessage? {
            if (ctx.side == Side.CLIENT) {
                val tile = FMLClientHandler.instance().worldClient.getTileEntity(message.tilePos)
                if (tile is TileTDLBase) {
                    tile.receiveSyncPacketFromServer(message)
                }
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
        val VEC3I_INDEX: Byte = 7
        val LONG_INDEX: Byte = 8
        val SHORT_INDEX: Byte = 9
    }
}