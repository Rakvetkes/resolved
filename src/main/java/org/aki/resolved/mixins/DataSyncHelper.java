package org.aki.resolved.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.world.chunk.Chunk;
import org.aki.resolved.Registered;
import org.aki.resolved.chunk.FluidChunk;
import org.aki.resolved.layer.ConstituentRegistry;
import org.aki.resolved.layer.FluidLayerSet;
import org.aki.resolved.layer.StateModelRegistry;

public final class DataSyncHelper {

    public static void onBlockStateGenerated(Chunk chunk, BlockState state, int i, int j, int k) {
        FluidChunk fluidChunk = Registered.FLUID_DATA.get(chunk);
        if (!state.isAir() && !(state.getBlock() instanceof FluidBlock)) {
            fluidChunk.setFluidData(i, j, k, StateModelRegistry.REGISTRY.containsKey(state.getBlock()) ?
                    StateModelRegistry.REGISTRY.get(state.getBlock()).getModel(state)
                    : new FluidLayerSet(Registered.CONSTITUENT_SOLID));
        }
        int fluidConsId = ConstituentRegistry.REGISTRY.get(state.getFluidState().getFluid());
        if (fluidConsId > 0) {
            FluidLayerSet data = fluidChunk.getFluidData(i, j, k).getMutable();
            data.fill(fluidConsId);
            fluidChunk.setFluidData(i, j, k, data);
        }
    }

    public static BlockState beforeSetBlockState(BlockState state) {
        // todo mixins required for waterlogged blocks
        if (state.getBlock() instanceof FluidBlock)
            state = Registered.RESOLVED_FLUID_BLOCK.getDefaultState();
        return state;
    }

}
