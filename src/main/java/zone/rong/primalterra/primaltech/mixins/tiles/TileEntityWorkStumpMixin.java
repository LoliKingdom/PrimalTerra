package zone.rong.primalterra.primaltech.mixins.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import primal_tech.tiles.TileEntityWorkStump;
import zone.rong.primalterra.primaltech.PlayerSensitiveHitSetter;

@Mixin(TileEntityWorkStump.class)
public class TileEntityWorkStumpMixin implements PlayerSensitiveHitSetter {

    @Shadow public boolean hit;
    @Unique private EntityPlayer player;

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/crafting/CraftingManager;getRemainingItems(Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/world/World;)Lnet/minecraft/util/NonNullList;"))
    private NonNullList<ItemStack> whenGettingRemainingItems(InventoryCrafting craftMatrix, World world) {
        ForgeHooks.setCraftingPlayer(this.player);
        NonNullList<ItemStack> remainder = CraftingManager.getRemainingItems(craftMatrix, world);
        ForgeHooks.setCraftingPlayer(this.player = null);
        return remainder;
    }

    @Override
    public void setHit(boolean isHit, EntityPlayer player) {
        this.hit = isHit;
        this.player = player;
    }

}
