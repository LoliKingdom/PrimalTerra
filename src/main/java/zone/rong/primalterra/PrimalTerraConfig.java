package zone.rong.primalterra;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

// TODO
@Deprecated
public final class PrimalTerraConfig {

    public static final PrimalTerraConfig INSTANCE;

    static {
        INSTANCE = new PrimalTerraConfig();
    }

    static void instantiate() {
        PrimalTerraLogger.LOGGER.info("Initializing config...");
    }

    private final Configuration config;

    public boolean pt$disableBed = true;
    public boolean pt$mimicTFCBed = true;
    public boolean pc$shareTorchFunctionalities = true;

    private PrimalTerraConfig() {
        config = new Configuration(new File(Loader.instance().getConfigDir(), "primalterra.cfg"));

        pt$disableBed = config.get("primaltech", "disableBed", true).getBoolean();
        pt$mimicTFCBed = config.get("primaltech", "mimicTFCBed", true).getBoolean();
        pc$shareTorchFunctionalities = config.get("primalcore", "shareTorchFunctionalities", true).getBoolean();
    }

}
