package zone.rong.primalterra.bwm;

import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HibachiTileEntity extends TETickCounter {

    private final HibachiInventory inventory = new HibachiInventory();

    private long endBurnTick;

    protected ItemStack getInventoryStack() {
        return inventory.getStackInSlot(0);
    }

    public void transferInventoryStack(EntityPlayer player, boolean toTile) {
        if (toTile) {
            player.setHeldItem(EnumHand.MAIN_HAND, inventory.insertItem(0, player.getHeldItemMainhand(), false));
        } else {
            ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(0, inventory.getSlotLimit(0), false), player.inventory.currentItem);
        }
    }

    public boolean burn() {
        if (endBurnTick > 0) { // There's an operation currently
            if (this.getTicksSinceUpdate() > endBurnTick) { // The operation should end
                return attemptBurn();
            }
        } else { // There's no operation currently and we should check if an operation could be started
            return attemptBurn();
        }
        return true;
    }

    private boolean attemptBurn() {
        ItemStack fuelStack = getInventoryStack();
        if (fuelStack.isEmpty()) {
            return false; // Nothing in the inventory = No operations performed
        }
        endBurnTick = TileEntityFurnace.getItemBurnTime(fuelStack);
        inventory.extractItem(0, 1, false); // Subtract from the fuelStack
        this.resetCounter();
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
        this.endBurnTick = nbt.getLong("EndBurnTick");
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("inventory", inventory.serializeNBT());
        nbt.setLong("EndBurnTick", this.endBurnTick);
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

    public void onBreakBlock(World world, BlockPos pos) {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(0));
    }

}
