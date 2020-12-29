package zone.rong.primalterra.bwm.mixins;

import betterwithmods.common.blocks.BlockHibachi;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zone.rong.primalterra.Utils;
import zone.rong.primalterra.bwm.HibachiTileEntity;

import javax.annotation.Nullable;
import java.util.Random;

@Mixin(BlockHibachi.class)
public abstract class BlockHibachiMixin extends Block implements ITileEntityProvider {

    @Shadow(remap = false) @Final public static PropertyBool LIT;

    protected BlockHibachiMixin() {
        super(Material.AIR);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void noRandomTicks(CallbackInfo ci) {
        this.setTickRandomly(false);
    }

    @Override
    @Nullable
    public TileEntity createNewTileEntity(World world, int meta) {
        return new HibachiTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float x, float y, float z) {
        HibachiTileEntity hibachi = Utils.getTile(world, pos, HibachiTileEntity.class);
        if (hibachi == null) {
            return false;
        }
        hibachi.transferStack(player, !player.isSneaking());
        return true;
    }

    /**
     * @author Rongmario
     * @reason Tick in the TE
     */
    @Overwrite
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) { }

    /**
     * @author Rongmario
     * @reason Tick in the TE
     */
    @Overwrite
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {

    }

    /**
     * @author Rongmario
     * @reason TODO: Slight Change
     */
    @Overwrite
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) {
        if (!world.isRemote) {
            HibachiTileEntity hibachi = Utils.getTile(world, pos, HibachiTileEntity.class);
            if (hibachi == null) {
                return;
            }
            if (state.getValue(LIT) && block instanceof BlockFire && other.getY() > pos.getY() && shouldIgnite(world, other)) {
                world.setBlockState(other, Blocks.FIRE.getDefaultState());
            }
            hibachi.attemptBurn();
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        HibachiTileEntity hibachi = Utils.getTile(world, pos, HibachiTileEntity.class);
        if (hibachi != null) {
            hibachi.onBreak();
        }
        super.breakBlock(world, pos, state);
    }

    /**
     * @author Rongmario
     * @reason Take flammable blocks with you!
     */
    @Overwrite(remap = false)
    private boolean shouldIgnite(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block instanceof BlockFire || block.isReplaceable(world, pos) || block.isFlammable(world, pos, EnumFacing.DOWN);
    }

}
