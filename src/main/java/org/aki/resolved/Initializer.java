package org.aki.resolved;

import net.fabricmc.api.ModInitializer;
import org.aki.resolved.chunk.FluidChunk;
import org.aki.resolved.misc.CommandGetBlock;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentInitializer;

public class Initializer implements ModInitializer, ChunkComponentInitializer {

    @Override
    public void onInitialize() {
        Registered.registerAll();
        CommandGetBlock.onInitialize();
    }

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(Registered.FLUID_DATA, FluidChunk::new);
    }

}
