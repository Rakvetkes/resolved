package org.aki.resolved.fluiddata;

import org.aki.resolved.Registered;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentInitializer;


public class FluidChunkDataInitializer implements ChunkComponentInitializer {

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(Registered.FLUID_DATA, FluidChunkData::new);
//        System.out.println("hello");
    }

}
