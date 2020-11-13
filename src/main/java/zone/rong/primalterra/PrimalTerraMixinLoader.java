package zone.rong.primalterra;

import org.spongepowered.asm.mixin.Mixins;
import zone.rong.mixinbooter.MixinLoader;

@MixinLoader
public class PrimalTerraMixinLoader {

    {
        Mixins.addConfiguration("mixins.primaltech.json");
    }

}
