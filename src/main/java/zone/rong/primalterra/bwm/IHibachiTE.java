package zone.rong.primalterra.bwm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHibachiTE {

    void transferInventoryStack(EntityPlayer player, boolean toTile);

    boolean burn();

    void onBreakBlock(World world, BlockPos pos);

}
