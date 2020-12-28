package zone.rong.primalterra.bwm;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class HibachiInventory extends ItemStackHandler {

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (!TileEntityFurnace.isItemFuel(stack)) {
            return;
        }
        super.setStackInSlot(slot, stack);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!TileEntityFurnace.isItemFuel(stack)) {
            return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        ItemStack slotStack = this.stacks.get(slot);
        if (slotStack.isEmpty()) {
            return 64;
        }
        return slotStack.getMaxStackSize();
    }

}

/*
public class HibachiInventory implements IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {

    private ItemStack stack;

    public HibachiInventory() {
        this.stack = ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot != 0) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
        }
        this.stack = stack;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot != 0) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
        }
        return stack;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack newStack, boolean simulate) {
        if (slot != 0) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
        }
        if (newStack.isEmpty() || !isItemValid(slot, newStack) || !ItemHandlerHelper.canItemStacksStack(newStack, this.stack)) {
            return newStack;
        }
        int limit = this.stack.isEmpty() ? getSlotLimit(slot) : getSlotLimit(slot) - this.stack.getCount();
        if (limit <= 0) {
            return newStack;
        }
        boolean reachedLimit = newStack.getCount() > limit;
        if (!simulate) {
            if (this.stack.isEmpty()) {
                this.stack = ItemHandlerHelper.copyStackWithSize(newStack, limit);
            } else {
                this.stack.grow(reachedLimit ? limit : stack.getCount());
            }
        }
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot != 0) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
        }
        if (amount == 0 || this.stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int toExtract = Math.min(amount, this.stack.getMaxStackSize());
        ItemStack returnStack;
        if (this.stack.getCount() <= toExtract) {
            returnStack = this.stack.copy();
            if (!simulate) {
                this.stack = ItemStack.EMPTY;
            }
        } else {
            returnStack = ItemHandlerHelper.copyStackWithSize(this.stack, toExtract);
            if (!simulate) {
                this.stack = ItemHandlerHelper.copyStackWithSize(this.stack, this.stack.getCount() - toExtract);
            }
        }
        return returnStack;
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot != 0) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
        }
        return 16;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot != 0) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0,1)");
        }
        return ForgeEventFactory.getItemBurnTime(stack) > 0;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return this.stack.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.stack = new ItemStack(nbt);
    }

}

 */
