package org.aki.resolved.mgr;

import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentInitializer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;


public class FluidChunkDataRegistry implements ChunkComponentInitializer {
    public static final ComponentKey<FluidChunkData> FLUID_DATA = ComponentRegistry.getOrCreate(Identifier.of("resolved", FluidChunkData.ID), FluidChunkData.class);
    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(FLUID_DATA, it->new FluidChunkData());
//        System.out.println("hello");
    }
}
