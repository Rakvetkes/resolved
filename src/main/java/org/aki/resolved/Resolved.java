package org.aki.resolved;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.Chunk;
import org.aki.resolved.common.PublicVars;
import org.aki.resolved.fluidblock.ResolvedFluid;
import org.aki.resolved.fluidblock.ResolvedFluidBlock;
import org.aki.resolved.manager.FluidServerManager;
import org.aki.resolved.manager.FluidManagerRegistry;

import java.lang.reflect.Method;

public class Resolved implements ModInitializer {

    @Override
    public void onInitialize() {
        PublicVars.RESOLVED_FLUID = Registry.register(Registries.FLUID,
                Identifier.of("resolved", "fluid"),
                new ResolvedFluid());
        PublicVars.RESOLVED_FLUID_BLOCK = Registry.register(Registries.BLOCK,
                Identifier.of("resolved", "fluid"),
                new ResolvedFluidBlock(PublicVars.RESOLVED_FLUID, Blocks.WATER.getSettings()));

        ServerWorldEvents.LOAD.register((server, world) -> {
            FluidManagerRegistry.REGISTRY.register(world.getRegistryKey().getValue(), new FluidServerManager(world));
        });
//        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
//            FluidServerManager fm = FluidManagerRegistry.REGISTRY.get(world);
//            fm.onChunkLoading(chunk);
//        });
//        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
//            FluidServerManager fm = FluidManagerRegistry.REGISTRY.get(world);
//            fm.onChunkUnloading(chunk);
//        });
//        ServerTickEvents.END_WORLD_TICK.register(world -> {
//            FluidManagerRegistry.REGISTRY.get(world).tick();
//        });
    }

}
