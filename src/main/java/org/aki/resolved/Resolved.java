package org.aki.resolved;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.aki.resolved.common.PublicVars;
import org.aki.resolved.fluidblock.ResolvedFluid;
import org.aki.resolved.fluidblock.ResolvedFluidBlock;
import org.aki.resolved.mgr.FluidServerManager;
import org.aki.resolved.mgr.FluidManagerRegistry;

import java.nio.file.Path;

public class Resolved implements ModInitializer {

    @Override
    public void onInitialize() {
        PublicVars.RESOLVED_FLUID = Registry.register(Registries.FLUID,
                Identifier.of("resolved", "fluid"),
                new ResolvedFluid());
        PublicVars.RESOLVED_FLUID_BLOCK = Registry.register(Registries.BLOCK,
                Identifier.of("resolved", "fluid"),
                new ResolvedFluidBlock(PublicVars.RESOLVED_FLUID, Blocks.WATER.getSettings()));
        try {
            System.out.println(AbstractBlock.Settings.class.getField("opaque").get(Blocks.WATER.getSettings()));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        ServerWorldEvents.LOAD.register((server, world) -> {
            FluidManagerRegistry.REGISTRY.register(world.getRegistryKey().getValue(), new FluidServerManager());
        });
//        ServerWorldEvents.UNLOAD.register((server, world) -> {
//            FluidManager.save(getDimensionPath(world), FluidManagerRegistry.REGISTRY.get(world.getRegistryKey().getValue()));
//        });
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            FluidManagerRegistry.REGISTRY.get(world).tick();
        });
        FluidManagerRegistry.registerForServer();
    }

    private Path getDimensionPath(ServerWorld world) {
        return world.getServer().getSavePath(WorldSavePath.ROOT).resolve(Path.of(worldPathConverter(world.getRegistryKey().getValue())));
    }

    private String worldPathConverter(Identifier identifier) {
        if (identifier.getNamespace().equals("minecraft")) {
            switch (identifier.getPath()) {
                case "overworld": return ".";
                case "the_end": return "DIM1";
                case "the_nether": return "DIM-1";
            }
            throw new IllegalArgumentException();
        } else {
            return "dimensions/" + identifier.getNamespace() + "/"
                    + identifier.getPath().replaceAll(".", "/");
        }
    }

}
