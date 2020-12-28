package zone.rong.primalterra;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class Utils {

    @SuppressWarnings("unchecked")
    public static <T> T getTile(IBlockAccess world, BlockPos pos, Class<T> aClass) {
        TileEntity te = world.getTileEntity(pos);
        return aClass.isInstance(te) ? (T) te : null;
    }

}
