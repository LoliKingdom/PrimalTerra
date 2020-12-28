package zone.rong.primalterra.bwm.tfc.mixins;

import io.netty.util.internal.UnstableApi;
import net.dries007.tfc.objects.te.TEInventory;
import net.dries007.tfc.objects.te.TEPlacedItem;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import zone.rong.primalterra.bwm.tfc.PlacedItemProgress;

@UnstableApi
@Mixin(TEPlacedItem.class)
public abstract class TEPlacedItemMixin extends TEInventory implements PlacedItemProgress {

    @Unique private byte progress = 0b0000;

    protected TEPlacedItemMixin() {
        super(0);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.progress = nbt.getByte("Progress");
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setByte("Progress", this.progress);
        return super.writeToNBT(nbt);
    }

    @Override
    public boolean state(int slot) {
        return ((progress >> --slot) & 1) == 1;
    }

    @Override
    public void on(int slot) {
        progress ^= 1 << --slot;
    }

    @Override
    public void off(int slot) {
        progress ^= 1 << --slot;
    }

}
