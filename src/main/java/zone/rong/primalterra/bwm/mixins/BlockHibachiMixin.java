package zone.rong.primalterra.bwm.mixins;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BlockHibachi;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import zone.rong.primalterra.Utils;
import zone.rong.primalterra.bwm.HibachiTileEntity;
import zone.rong.primalterra.bwm.IHibachiTE;
import zone.rong.primalterra.bwm.tfc.TFCHibachiTileEntity;

import javax.annotation.Nullable;
import java.util.Random;

@Mixin(BlockHibachi.class)
public abstract class BlockHibachiMixin extends Block implements ITileEntityProvider {

    @Shadow protected abstract void setLit(World world, BlockPos pos);
    @Shadow protected abstract void clearLit(World world, BlockPos pos);

    @Shadow public abstract boolean isLit(IBlockAccess world, BlockPos pos);

    protected BlockHibachiMixin() {
        super(Material.ROCK);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return Loader.isModLoaded("tfc") ? new TFCHibachiTileEntity() : new HibachiTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float x, float y, float z) {
        IHibachiTE hibachi = Utils.getTile(world, pos, IHibachiTE.class);
        if (hibachi == null) {
            return false;
        }
        hibachi.transferInventoryStack(player, !player.isSneaking());
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        IHibachiTE hibachi = Utils.getTile(world, pos, IHibachiTE.class);
        if (hibachi != null) {
            hibachi.onBreakBlock(world, pos);
        }
        super.breakBlock(world, pos, state);
    }

    /**
     * @author Rongmario
     * @reason Much more efficient method in randomTick
     */
    @Overwrite
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        randomTick(world, pos, state, rand);
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) {
            return;
        }
        IHibachiTE hibachi = Utils.getTile(world, pos, IHibachiTE.class);
        if (hibachi == null || world.getRedstonePowerFromNeighbors(pos) <= 0) {
            this.extinguish(world, pos);
            return;
        }
        if (hibachi.burn()) {
            this.ignite(world, pos);
        } else {
            this.extinguish(world, pos);
        }
    }

    /**
     * @author Rongmario
     * @reason Fixed a little on the logic
     */
    @Overwrite(remap = false)
    private void ignite(World world, BlockPos pos) {
        if (!this.isLit(world, pos)) {
            this.setLit(world, pos);
            BlockPos up = pos.up();
            if (this.shouldIgnite(world, up)) {
                world.playSound(null, up, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.4F + 1.0F);
                world.setBlockState(up, Blocks.FIRE.getDefaultState());
            }
        }
    }

    /**
     * @author Rongmario
     * @reason Fixed a little on the logic
     */
    @Overwrite(remap = false)
    private void extinguish(World world, BlockPos pos) {
        if (this.isLit(world, pos)) {
            this.clearLit(world, pos);
            BlockPos up = pos.up();
            Block upBlock = world.getBlockState(up).getBlock();
            if (upBlock == Blocks.FIRE || upBlock == BWMBlocks.STOKED_FLAME) {
                world.playSound(null, up, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
                world.setBlockToAir(up);
            }
        }
    }

    /**
     * @author Rongmario
     * @reason Fixed a little on the logic
     */
    @Overwrite(remap = false)
    private boolean shouldIgnite(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block.isReplaceable(world, pos) || block.isFlammable(world, pos, EnumFacing.DOWN);
    }

    /*
    @Deprecated
    @Redirect(method = "updateTick", at = @At(value = "INVOKE", target = "Lbetterwithmods/common/blocks/BlockHibachi;ignite(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", remap = false))
    private void onIgnite(BlockHibachi block, World world, BlockPos pos) {
        HibachiTileEntity hibachi = Utils.getTile(world, pos, HibachiTileEntity.class);
        if (hibachi != null) {
            ItemStack fuelStack = hibachi.getInventoryStack();
            if (!fuelStack.isEmpty()) {
                int burnTime = TileEntityFurnace.getItemBurnTime(fuelStack);
            }
        }
    }
     */

}
