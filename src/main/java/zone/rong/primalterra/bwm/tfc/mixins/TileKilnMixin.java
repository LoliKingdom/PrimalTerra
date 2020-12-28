package zone.rong.primalterra.bwm.mixins;

import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.BlockKiln;
import betterwithmods.common.blocks.tile.TileKiln;
import betterwithmods.common.registry.block.recipe.KilnRecipe;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.objects.inventory.capability.ItemStackHandlerCallback;
import net.dries007.tfc.objects.te.TEPlacedItem;
import net.dries007.tfc.util.Helpers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import zone.rong.primalterra.bwm.PlacedItemProgress;

import java.util.Random;
import java.util.function.Supplier;

import static net.dries007.tfc.Constants.RNG;

@Mixin(TileKiln.class)
public abstract class TileKilnMixin extends TileEntity {

    /**
     * @author Rongmario
     * @reason Implement TFC kiln recipes in BWM's kilns
     */
    @Overwrite(remap = false)
    public void kiln(World world, BlockPos craftPos, Random rand) {
        BlockPos kilnPos = this.pos;
        Block block = world.getBlockState(kilnPos).getBlock();
        if (block instanceof BlockKiln) {
            BlockKiln kiln = (BlockKiln) block;
            int oldCookTime = kiln.getCookCounter(world, kilnPos);
            int currentTickRate = 20;
            IBlockState state = world.getBlockState(craftPos);
            KilnRecipe recipe = BWRegistry.KILN.findRecipe(world, craftPos, state).orElse(null);
            if (recipe != null) {
                int newCookTime = oldCookTime + 1;
                if (newCookTime > 7) {
                    newCookTime = 0;
                    recipe.craftRecipe(world, craftPos, rand, state);
                    kiln.setCookCounter(world, kilnPos, 0);
                } else {
                    if (newCookTime > 0) {
                        world.sendBlockBreakProgress(0, craftPos, newCookTime + 2);
                    }
                    currentTickRate = kiln.calculateTickRate(world, kilnPos);
                }
                kiln.setCookCounter(world, kilnPos, newCookTime);
                if (newCookTime == 0) {
                    world.sendBlockBreakProgress(0, craftPos, -1);
                    kiln.setCookCounter(world, kilnPos, 0);
                    world.scheduleBlockUpdate(kilnPos, block, currentTickRate, 5);
                }
            } else {
                TEPlacedItem placedItem = Helpers.getTE(world, craftPos, TEPlacedItem.class);
                if (placedItem != null) {
                    IItemHandler inv = placedItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                    if (inv != null) {
                        boolean cooking = false;
                        Int2ObjectMap<Supplier<ItemStack>> recipeOutputs = null;
                        for (int i = 0; i < inv.getSlots(); i++) {
                            ItemStack stack = inv.getStackInSlot(i);
                            IItemHeat outputHeat = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                            if (outputHeat != null) {
                                HeatRecipe heatRecipe = HeatRecipe.get(stack, Metal.Tier.TIER_I);
                                if (heatRecipe != null) {
                                    cooking = true;
                                    if (recipeOutputs == null) {
                                        ((PlacedItemProgress) placedItem).on(i);
                                        recipeOutputs = new Int2ObjectOpenHashMap<>(inv.getSlots() - i);
                                    }
                                    recipeOutputs.put(i, () -> heatRecipe.getOutputStack(stack));
                                }
                                placedItem.markForSync();
                            }
                        }
                        if (cooking) {
                            int newCookTime = oldCookTime + 1;
                            if (newCookTime > 7) {
                                newCookTime = 0;
                                recipeOutputs.forEach((i, r) -> {
                                    ItemStack outputStack = r.get();
                                    IItemHeat outputHeat = outputStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                                    if (outputHeat != null) {
                                        outputHeat.setTemperature(Heat.maxVisibleTemperature());
                                    }
                                    ((ItemStackHandlerCallback) inv).setStackInSlot(i, outputStack);
                                    ((PlacedItemProgress) placedItem).off(i);
                                });
                                placedItem.markForSync();
                                kiln.setCookCounter(world, kilnPos, 0);
                            } else {
                                if (newCookTime > 0) {
                                    double x = craftPos.getX() + 0.5, y = craftPos.getY() + 0.1, z = craftPos.getZ() + 0.5;
                                    switch (RNG.nextInt(3)) {
                                        case 0:
                                            TFCParticles.FIRE_PIT_SMOKE1.spawn(world, x, y, z, 0, 0.1D, 0, 120);
                                            break;
                                        case 1:
                                            TFCParticles.FIRE_PIT_SMOKE2.spawn(world, x, y, z, 0, 0.1D, 0, 110);
                                            break;
                                        case 2:
                                            TFCParticles.FIRE_PIT_SMOKE3.spawn(world, x, y, z, 0, 0.1D, 0, 100);
                                            break;
                                    }
                                    // world.sendBlockBreakProgress(0, craftPos, newCookTime + 2);
                                }
                                currentTickRate = kiln.calculateTickRate(world, kilnPos);
                            }
                            kiln.setCookCounter(world, kilnPos, newCookTime);
                            if (newCookTime == 0) {
                                // world.sendBlockBreakProgress(0, craftPos, -1);
                                kiln.setCookCounter(world, kilnPos, 0);
                                world.scheduleBlockUpdate(kilnPos, block, currentTickRate, 5);
                            }
                        }
                    }
                } else if (oldCookTime != 0) {
                    world.sendBlockBreakProgress(0, craftPos, -1);
                    kiln.setCookCounter(world, kilnPos, 0);
                    world.scheduleBlockUpdate(kilnPos, block, currentTickRate, 5);
                } else {
                    world.sendBlockBreakProgress(0, craftPos, -1);
                }
            }
        }

    }

}
