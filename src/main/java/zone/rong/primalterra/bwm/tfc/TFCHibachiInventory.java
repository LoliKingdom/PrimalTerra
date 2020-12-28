package zone.rong.primalterra.bwm.tfc;

import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.minecraft.item.ItemStack;
import zone.rong.primalterra.bwm.HibachiInventory;

import javax.annotation.Nonnull;

public class TFCHibachiInventory extends HibachiInventory {

    @Override
    public int getSlotLimit(int slot) {
        ItemStack slotStack = this.stacks.get(slot);
        if (slotStack.isEmpty()) {
            return 64;
        }
        IItemSize size = CapabilityItemSize.getIItemSize(slotStack);
        if (size != null) {
            return size.getStackSize(slotStack);
        }
        return slotStack.getMaxStackSize();
    }

    @Override
    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        IItemSize size = CapabilityItemSize.getIItemSize(stack);
        if (size != null) {
            return Math.min(getSlotLimit(slot), size.getStackSize(stack));
        }
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

}
