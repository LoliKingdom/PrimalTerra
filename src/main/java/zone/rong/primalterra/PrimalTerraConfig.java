package zone.rong.primalterra;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

public final class PrimalTerraConfig {

    public static final PrimalTerraConfig INSTANCE;

    static {
        INSTANCE = new PrimalTerraConfig();
    }

    static void instantiate() {
        PrimalTerra.LOGGER.info("Initializing config...");
    }

    private Configuration config;

    public boolean shareTorchFunctionalities = true;

    private PrimalTerraConfig() {
        config = new Configuration(new File(Loader.instance().getConfigDir(), "primalterra.cfg"));

        shareTorchFunctionalities = config.get("shareTorchFunctionalities", "bridge", true).getBoolean();
    }

}
