package zone.rong.primalterra.pyrotech.mixins;

import betterwithmods.common.BWMBlocks;
import com.codetaylor.mc.pyrotech.IAirflowConsumerCapability;
import com.codetaylor.mc.pyrotech.modules.core.ModuleCore;
import com.codetaylor.mc.pyrotech.modules.tech.machine.tile.TileBellows;
import net.dries007.tfc.api.util.IBellowsConsumerBlock;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TileBellows.class)
public abstract class TileBellowsMixin extends TileEntity {

    @Shadow protected abstract float getAirflow();

    /**
     * @author Rongmario
     * @reason Integrating BWM + TFC behaviours
     */
    @Overwrite(remap = false)
    protected void pushAirflow(BlockPos blockPos, EnumFacing facing) {
        TileEntity te = this.world.getTileEntity(blockPos);
        if (te != null) {
            IAirflowConsumerCapability airflowConsumer = te.getCapability(ModuleCore.CAPABILITY_AIRFLOW_CONSUMER, facing.getOpposite());
            if (airflowConsumer != null) {
                airflowConsumer.consumeAirflow(this.getAirflow(), false);
            }
            return;
        }
        Block block = this.world.getBlockState(blockPos).getBlock();
        if (block instanceof IBellowsConsumerBlock) {
            ((IBellowsConsumerBlock) block).onAirIntake(null, this.world, blockPos, (int) this.getAirflow() * 250);
            return;
        }
        if (block == Blocks.FIRE || block == BWMBlocks.STOKED_FLAME) {
            if (world.getBlockState(blockPos.down()).getBlock() == BWMBlocks.HIBACHI) {
                int flag = block == BWMBlocks.STOKED_FLAME ? 4 : 3;
                world.setBlockState(blockPos, BWMBlocks.STOKED_FLAME.getDefaultState(), flag);
            } else {
                world.setBlockToAir(blockPos);
            }
        }
    }

}