package zone.rong.primalterra;

import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixins;
import zone.rong.mixinbooter.MixinLoader;

@MixinLoader
public class PrimalTerraMixinLoader {

    static {
        PrimalTerraConfig.instantiate();
    }

    {
        if (Loader.isModLoaded("primal_tech")) {
            PrimalTerra.LOGGER.info("Primal_Tech found, loading its mixins...");
            Mixins.addConfiguration("mixins.primaltech.json");
        }
        if (Loader.isModLoaded("primalcore")) {
            Mixins.addConfiguration("mixins.primalcore.json");
        }
        if (PrimalTerraConfig.INSTANCE.shareTorchFunctionalities) {
            PrimalTerra.LOGGER.info("shareTorchFunctionalities is true, loading its mixins...");
        }
    }

}
