package zone.rong.primalterra.bwm.mixins;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.EnumTier;
import betterwithmods.common.blocks.mechanical.BlockBellows;
import betterwithmods.util.DirUtils;
import com.codetaylor.mc.pyrotech.IAirflowConsumerCapability;
import com.codetaylor.mc.pyrotech.modules.core.ModuleCore;
import net.dries007.tfc.api.util.IBellowsConsumerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockBellows.class)
public abstract class BlockBellowsMixin {

    @Shadow public abstract EnumFacing getFacing(IBlockState state);
    @Shadow public abstract void blowItem(BlockPos pos, EnumFacing facing, EntityItem item);
    @Shadow public abstract EnumTier getTier(World world, BlockPos pos);
    @Shadow protected abstract void stokeFire(World world, BlockPos pos);

    /**
     * @author Rongmario
     * @reason Implement TFC/Pyrotech bellows behaviours whilst optimizing existing BWM behaviour
     */
    @Overwrite(remap = false)
    public void blow(World world, BlockPos pos) {
        EnumFacing facing = this.getFacing(world.getBlockState(pos));
        BlockPos pos2 = pos.offset(facing, 4);
        AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(),pos2.getX() + 1, pos2.getY() + 1, pos2.getZ() + 1);
        world.getEntitiesWithinAABB(EntityItem.class, box).forEach(i -> this.blowItem(pos, facing, i));
        EnumFacing dirLeft = DirUtils.rotateFacingAroundY(facing, false);
        EnumFacing dirRight = DirUtils.rotateFacingAroundY(facing, true);
        for (int i = 1; i <= 3; i++) {
            BlockPos dirPos = pos.offset(facing, i);
            Block target = world.getBlockState(dirPos).getBlock();
            if (target != Blocks.FIRE && target != BWMBlocks.STOKED_FLAME) {
                if (!world.isAirBlock(dirPos)) {
                    break;
                }
            } else {
                this.stokeFire(world, dirPos);
            }
            // To the left, to the right
            // Step it up, step it up it's alright
            this.stoke(facing, world, dirPos.offset(dirLeft));
            this.stoke(facing, world, dirPos.offset(dirRight));
        }
    }

    /**
     * But can the bellows do it on a cold, rainy night at Stoke?
     */
    private void stoke(EnumFacing mainFace, World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof IBellowsConsumerBlock) {
            ((IBellowsConsumerBlock) block).onAirIntake(null, world, pos, (this.getTier(world, pos) == EnumTier.STEEL ? 3 : 1) * 200);
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            IAirflowConsumerCapability consumer = tile.getCapability(ModuleCore.CAPABILITY_AIRFLOW_CONSUMER, mainFace.getOpposite());
            if (consumer != null) {
                consumer.consumeAirflow(this.getTier(world, pos) == EnumTier.STEEL ? 1.0F : 0.5F, false);
                return;
            }
        }
        BlockPos downPos = pos.down();
        Block downBlock = world.getBlockState(downPos).getBlock();
        if (downBlock instanceof IBellowsConsumerBlock) {
            ((IBellowsConsumerBlock) downBlock).onAirIntake(null, world, downPos, (this.getTier(world, pos) == EnumTier.STEEL ? 3 : 1) * 200);
        } else if (block == Blocks.FIRE || block == BWMBlocks.STOKED_FLAME) {
            if (downBlock == BWMBlocks.HIBACHI) {
                world.setBlockState(pos, BWMBlocks.STOKED_FLAME.getDefaultState(), world.getBlockState(pos).getBlock() == Blocks.FIRE ? 3 : 4);
            } else {
                world.setBlockToAir(pos);
            }
        }
    }

}
