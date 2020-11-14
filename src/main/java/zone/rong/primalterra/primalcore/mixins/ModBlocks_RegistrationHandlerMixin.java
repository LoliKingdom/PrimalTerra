package zone.rong.primalterra.primalcore.mixins;

import nmd.primal.core.common.init.ModBlocks;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModBlocks.RegistrationHandler.class)
public class ModBlocks_RegistrationHandlerMixin {

    @Redirect(method = "registerBlocks", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnmd/primal/core/common/init/ModConfig$Survival;REPLACE_VANILLA_TORCHES:Z", remap = false), remap = false)
    private static boolean overrideVanillaTorch() {
        return false;
    }

    @Redirect(method = "registerItemBlocks", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnmd/primal/core/common/init/ModConfig$Survival;REPLACE_VANILLA_TORCHES:Z", remap = false), remap = false)
    private static boolean overrideVanillaTorchItem() {
        return false;
    }

}
