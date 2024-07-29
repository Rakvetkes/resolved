package org.aki.resolved.fluiddata;

import org.aki.resolved.Registered;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentInitializer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;


public class FluidChunkDataRegistry implements ChunkComponentInitializer {

    public static final ComponentKey<FluidChunkData> FLUID_DATA = ComponentRegistry.getOrCreate(Registered.Identifiers.FLUID_CHUNK_DATA, FluidChunkData.class);

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(FLUID_DATA, FluidChunkData::new);
//        System.out.println("hello");
    }

}
