package zone.rong.primalterra.tfc.mixins;

import betterwithmods.common.BWMBlocks;
import com.codetaylor.mc.pyrotech.IAirflowConsumerCapability;
import com.codetaylor.mc.pyrotech.modules.core.ModuleCore;
import net.dries007.tfc.api.util.IBellowsConsumerBlock;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.te.TEBase;
import net.dries007.tfc.objects.te.TEBellows;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

import static net.minecraft.block.BlockHorizontal.FACING;

@Mixin(TEBellows.class)
public class TEBellowsMixin extends TEBase {

    @Shadow private long lastPushed;

    @Shadow @Final private static Set<Vec3i> OFFSETS;

    @Shadow @Final private static int BELLOWS_AIR;

    /**
     * @author Rongmario
     * @reason Integrating BWM + Pyrotech behaviours
     */
    @Overwrite(remap = false)
    public boolean onRightClick() {
        long time = world.getTotalWorldTime() - lastPushed;
        if (time < 20) {
            return true;
        }
        world.playSound(null, pos, TFCSounds.BELLOWS_BLOW_AIR, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (!world.isRemote) {
            lastPushed = world.getTotalWorldTime();
            markForBlockUpdate();
        }
        EnumFacing direction = world.getBlockState(pos).getValue(FACING); // It is a better idea to inherit the direction directly from the block.
        for (Vec3i offset : OFFSETS) {
            BlockPos posx = pos.up(offset.getY()).offset(direction, offset.getX()).offset(direction.rotateY(), offset.getZ());
            Block block = world.getBlockState(posx).getBlock();
            if (block instanceof IBellowsConsumerBlock && ((IBellowsConsumerBlock) block).canIntakeFrom(null, offset, direction)) {
                ((IBellowsConsumerBlock) block).onAirIntake(null, world, posx, BELLOWS_AIR);
                if (world.isRemote) {
                    posx = pos.offset(direction);
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, posx.getX() + .5d, posx.getY() + .5d, posx.getZ() + .5d, 0, 0, 0);
                }
                return true;
            } else if (block == Blocks.FIRE || block == BWMBlocks.STOKED_FLAME) {
                if (world.getBlockState(posx.down()).getBlock() == BWMBlocks.HIBACHI) {
                    int flag = block == BWMBlocks.STOKED_FLAME ? 4 : 3;
                    world.setBlockState(posx, BWMBlocks.STOKED_FLAME.getDefaultState(), flag);
                } else {
                    world.setBlockToAir(posx);
                }
            } else {
                TileEntity te = this.world.getTileEntity(posx);
                if (te != null) {
                    IAirflowConsumerCapability airflowConsumer = te.getCapability(ModuleCore.CAPABILITY_AIRFLOW_CONSUMER, direction.getOpposite());
                    if (airflowConsumer != null) {
                        airflowConsumer.consumeAirflow(0.1F, false);
                    }
                }
            }
        }
        return true;
    }

}
