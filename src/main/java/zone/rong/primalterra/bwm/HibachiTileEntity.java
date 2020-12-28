package zone.rong.primalterra.bwm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HibachiTileEntity extends TileEntity implements ITickable, IHibachiTE {

    private final HibachiInventory inventory = new HibachiInventory();

    private long burnTime;

    protected ItemStack getInventoryStack() {
        return inventory.getStackInSlot(0);
    }

    @Override
    public void update() {
        burnTime--;
        burn();
    }

    @Override
    public void transferInventoryStack(EntityPlayer player, boolean toTile) {
        if (toTile) {
            player.setHeldItem(EnumHand.MAIN_HAND, inventory.insertItem(0, player.getHeldItemMainhand(), false));
        } else {
            ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(0, inventory.getSlotLimit(0), false), player.inventory.currentItem);
        }
    }

    @Override
    public boolean burn() {
        return burnTime > 0 || attemptBurn();
    }

    private boolean attemptBurn() {
        ItemStack fuelStack = getInventoryStack();
        if (fuelStack.isEmpty()) {
            return false; // Nothing in the inventory = No operations performed
        }
        burnTime = TileEntityFurnace.getItemBurnTime(fuelStack);
        inventory.extractItem(0, 1, false); // Subtract from the fuelStack
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
        this.burnTime = nbt.getLong("BurnTime");
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("inventory", inventory.serializeNBT());
        nbt.setLong("BurnTime", this.burnTime);
        return super.writeToNBT(nbt);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing != null && facing != EnumFacing.UP);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return facing == null || facing == EnumFacing.UP ? null : CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        }
        return null;
    }

    @Override
    public void onBreakBlock(World world, BlockPos pos) {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(0));
    }

}
