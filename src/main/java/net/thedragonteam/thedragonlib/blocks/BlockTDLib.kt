package net.thedragonteam.thedragonlib.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.thedragonteam.thedragonlib.api.IDataRetainerTile
import net.thedragonteam.thedragonlib.util.ItemNBTHelper

class BlockTDLib @JvmOverloads constructor(material: Material = Material.ROCK) : Block(material) {
    protected var isFullCube = true

    init {
        this.setHardness(5f)
        this.setResistance(10f)
    }

    //region Rename field names
    override fun getSubBlocks(itemIn: Item, tab: CreativeTabs?, list: NonNullList<ItemStack>) {
        super.getSubBlocks(itemIn, tab, list)
    }

    override fun onBlockPlacedBy(world: World?, pos: BlockPos?, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
        super.onBlockPlacedBy(world, pos, state, placer, stack)

        val tile = world!!.getTileEntity(pos!!)

        if (tile is IDataRetainerTile && ItemNBTHelper.getCompound(stack).hasKey(BlockTDLib.TILE_DATA_TAG)) {
            tile.readDataFromNBT(ItemNBTHelper.getCompound(stack).getCompoundTag(BlockTDLib.TILE_DATA_TAG))
        }
    }
    //endregion

    //region Setters & Getters
    fun setHarvestTool(toolClass: String, level: Int): BlockTDLib {
        this.setHarvestLevel(toolClass, level)
        return this
    }

    override fun getPickBlock(state: IBlockState, target: RayTraceResult?, world: World, pos: BlockPos, player: EntityPlayer?): ItemStack {
        val stack = super.getPickBlock(state, target, world, pos, player)

        if (stack.item === Item.getItemFromBlock(this) && stack.item.hasSubtypes) {
            stack.itemDamage = getMetaFromState(state)
        }

        val tileEntity = world.getTileEntity(pos)

        if (tileEntity is IDataRetainerTile) {
            val customData = NBTTagCompound()
            tileEntity.writeDataToNBT(customData)
            ItemNBTHelper.getCompound(stack).setTag(TILE_DATA_TAG, customData)
        }

        return stack
    }

    fun setIsFullCube(value: Boolean): BlockTDLib {
        isFullCube = value
        return this
    }

    //endregion

    override fun harvestBlock(world: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, heldStack: ItemStack) {
        if (te is IDataRetainerTile) {
            val stack = ItemStack(Item.getItemFromBlock(state.block))
            val customData = NBTTagCompound()
            te.writeDataToNBT(customData)
            ItemNBTHelper.getCompound(stack).setTag(TILE_DATA_TAG, customData)
            Block.spawnAsEntity(world, pos, stack)
            world.removeTileEntity(pos)
        } else {
            super.harvestBlock(world, player, pos, state, te, heldStack)
        }
    }

    override fun isFullCube(state: IBlockState?): Boolean {
        return isFullCube
    }

    override fun isOpaqueCube(state: IBlockState?): Boolean {
        return isFullCube
    }

    companion object {
        val TILE_DATA_TAG = "SMTileData"
    }
}
