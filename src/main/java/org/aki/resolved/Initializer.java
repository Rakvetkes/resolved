package org.aki.resolved;

import net.fabricmc.api.ModInitializer;
import org.aki.resolved.chunk.FluidChunk;
import org.aki.resolved.misc.CommandGetBlock;
import org.aki.resolved.misc.CommandGetFluidData;
import org.aki.resolved.misc.CommandPutWater;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentInitializer;

public class Initializer implements ModInitializer, ChunkComponentInitializer {

    @Override
    public void onInitialize() {
        Registered.registerAll();
        CommandGetBlock.onInitialize();
        CommandGetFluidData.onInitialize();
        CommandPutWater.onInitialize();
    }

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(Registered.FLUID_DATA, FluidChunk::new);
    }

}
