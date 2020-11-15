package zone.rong.primalterra.primaltech.mixins.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import primal_tech.blocks.BlockWorkStump;
import primal_tech.tiles.TileEntityWorkStump;
import zone.rong.primalterra.primaltech.PlayerSensitiveHitSetter;

@Mixin(BlockWorkStump.class)
public class BlockWorkStumpMixin {

    @Redirect(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lprimal_tech/tiles/TileEntityWorkStump;setHit(Z)V", remap = false))
    private void setPlayerAndHit(TileEntityWorkStump tile, boolean isHit, World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        ((PlayerSensitiveHitSetter) tile).setHit(isHit, player);
    }

}
