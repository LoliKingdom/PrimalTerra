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
        if (Loader.isModLoaded("betterwithmods")) {
            PrimalTerraLogger.LOGGER.info("BetterWithMods found, loading its mixins...");
            Mixins.addConfiguration("mixins.bwm.json");
        }
        if (Loader.isModLoaded("primal_tech")) {
            PrimalTerraLogger.LOGGER.info("PrimalTech found, loading its mixins...");
            Mixins.addConfiguration("mixins.primaltech.json");
        }
        if (Loader.isModLoaded("primal")) {
            PrimalTerraLogger.LOGGER.info("PrimalCore found, loading its mixins...");
            Mixins.addConfiguration("mixins.primalcore.json");
        }
        if (PrimalTerraConfig.INSTANCE.pc$shareTorchFunctionalities) {
            PrimalTerraLogger.LOGGER.info("shareTorchFunctionalities is true, loading its mixins...");
        }
    }

}
