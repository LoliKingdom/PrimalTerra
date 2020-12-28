package zone.rong.primalterra;

import betterwithmods.BWMod;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import zone.rong.primalterra.bwm.HibachiTileEntity;

@Mod(modid = PrimalTerra.MOD_ID, name = PrimalTerra.NAME, version = "0.2", dependencies = "required:tfc")
public class PrimalTerra {

    public static final String MOD_ID = "primalterra";
    public static final String NAME = "PrimalTerra";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (Loader.isModLoaded(BWMod.MODID)) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        if (Loader.isModLoaded(BWMod.MODID)) {
            GameRegistry.registerTileEntity(HibachiTileEntity.class, new ResourceLocation(BWMod.MODID, "hibachi"));
        }
    }

}
