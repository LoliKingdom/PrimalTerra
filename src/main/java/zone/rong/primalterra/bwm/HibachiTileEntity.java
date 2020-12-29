package zone.rong.primalterra.bwm;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BlockHibachi;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import zone.rong.primalterra.bwm.tfc.TFCHibachiInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HibachiTileEntity extends TileEntity implements ITickable {

    private final HibachiInventory inventory = Loader.isModLoaded("tfc") ? new TFCHibachiInventory() : new HibachiInventory();

    private boolean burning = false;
    private int burnTime;

    @Override
    public void update() {
        if (this.burning) {
            this.burnTime--;
        }
        attemptBurn();
    }

    public void transferStack(EntityPlayer player, boolean toTile) {
        if (toTile) {
            player.setHeldItem(EnumHand.MAIN_HAND, inventory.insertItem(0, player.getHeldItemMainhand(), false));
        } else {
            ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(0, inventory.getSlotLimit(0), false), player.inventory.currentItem);
        }
        this.markDirty();
    }

    public void attemptBurn() {
        if (burnTime == 0) {
            burn();
        } else if (world.getRedstonePowerFromNeighbors(this.pos) <= 0) {
            this.burning = false;
            if (world.getBlockState(this.pos).getValue(BlockHibachi.LIT)) {
                world.setBlockState(this.pos, BWMBlocks.HIBACHI.getDefaultState());
                BlockPos upPos = this.pos.up();
                if (world.getBlockState(upPos).getBlock() instanceof BlockFire) {
                    world.setBlockToAir(upPos);
                }
            }
        } else if (!this.burning) {
            BlockPos upPos = this.pos.up();
            if (shouldIgnite(this.world, upPos)) {
                this.burning = true;
                world.setBlockState(this.pos, BWMBlocks.HIBACHI.getDefaultState().withProperty(BlockHibachi.LIT, true));
                world.setBlockState(upPos, Blocks.FIRE.getDefaultState());
            }
        }
    }

    protected void burn() {
        ItemStack fuelStack = this.inventory.getStackInSlot(0);
        BlockPos upPos;
        if (!fuelStack.isEmpty() && world.getRedstonePowerFromNeighbors(this.pos) > 0 && shouldIgnite(world, (upPos = pos.up()))) {
            this.burning = true;
            this.burnTime = TileEntityFurnace.getItemBurnTime(fuelStack);
            this.inventory.extractItem(0, 1, false);
            this.world.setBlockState(upPos, Blocks.FIRE.getDefaultState());
            this.world.setBlockState(this.pos, BWMBlocks.HIBACHI.getDefaultState().withProperty(BlockHibachi.LIT, true));
            this.markDirty();
        } else {
            this.burning = false;
            if (this.world.getBlockState(this.pos).getValue(BlockHibachi.LIT)) {
                this.world.setBlockState(this.pos, BWMBlocks.HIBACHI.getDefaultState());
                if (world.getBlockState((upPos = pos.up())).getBlock() instanceof BlockFire) {
                    world.setBlockToAir(upPos);
                }
            }
        }
    }

    public void onBreak() {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(0));
    }

    private boolean shouldIgnite(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block instanceof BlockFire || block.isReplaceable(world, pos) || block.isFlammable(world, pos, EnumFacing.DOWN);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
        this.burning = nbt.getBoolean("Burning");
        this.burnTime = nbt.getInteger("BurnTime");
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("inventory", this.inventory.serializeNBT());
        nbt.setBoolean("Burning", this.burning);
        nbt.setInteger("BurnTime", this.burnTime);
        return super.writeToNBT(nbt);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing != null && facing != EnumFacing.UP)) || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing != null && facing != EnumFacing.UP)) {
            return (T) inventory;
        }
        return super.getCapability(capability, facing);
    }

    /**
     * Gets the update packet that is used to sync the TE on load
     */
    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    /**
     * Gets the update tag send by packets. Contains base data (i.e. position), as well as TE specific data
     */
    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return nbt;
    }

    /**
     * Handles updating on client side when a block update is received
     */
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    /**
     * Reads the update tag attached to a chunk or TE packet
     */
    @Override
    public void handleUpdateTag(NBTTagCompound nbt) {
        readFromNBT(nbt);
    }

}
