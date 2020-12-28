package zone.rong.primalterra.bwm.mixins;

import betterwithmods.common.blocks.BlockHibachi;
import net.dries007.tfc.util.Helpers;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zone.rong.primalterra.bwm.HibachiTileEntity;

import javax.annotation.Nullable;
import java.util.Random;

@Mixin(BlockHibachi.class)
public abstract class BlockHibachiMixin extends Block implements ITileEntityProvider {

    protected BlockHibachiMixin() {
        super(Material.ROCK);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void noRandomTicks(CallbackInfo ci) {
        this.needsRandomTick = false;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new HibachiTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float x, float y, float z) {
        HibachiTileEntity hibachi = Helpers.getTE(world, pos, HibachiTileEntity.class);
        if (hibachi == null) {
            return false;
        }
        hibachi.transferInventoryStack(player, !player.isSneaking());
        return true;
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        HibachiTileEntity hibachi = Helpers.getTE(world, pos, HibachiTileEntity.class);
        if (hibachi != null) {
            ItemStack stack = hibachi.getInventoryStack();
            if (!stack.isEmpty()) {
                Helpers.spawnItemStack(world, pos, hibachi.getInventoryStack());
            }
        }
    }

    /**
     * @author Rongmario
     * @reason Deprecate onBlockAdded behaviour
     */
    @Overwrite
    @Deprecated
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) { }

    /**
     * @author Rongmario
     * @reason Deprecate updateTick behaviour
     */
    @Overwrite
    @Deprecated
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) { }

    /**
     * @author Rongmario
     * @reason Deprecate neighborChanged behaviour
     */
    @Overwrite
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) { }

}
