package org.aki.resolved.manager;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

public class FluidServerManager implements FluidDataAccessor {

    private final ServerWorld world;

    public FluidServerManager(ServerWorld world) {
        this.world = world;
    }

    public FluidChunkData getChunk(ChunkPos chunkPos) {
        Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
        if (chunk == null) throw new IllegalStateException();
        return FluidChunkDataRegistry.FLUID_DATA.get(chunk);
    }

    @Override
    public @NotNull FluidBlockData getFluidData(BlockPos pos) {
        FluidChunkData chunk = getChunk(new ChunkPos(pos));
        return chunk.get(pos.getX() & ((1 << FluidChunkData.EDGE_BITS) - 1), pos.getY(), pos.getZ() & ((1 << FluidChunkData.EDGE_BITS) - 1));
    }

    public int getColor(BlockPos pos) {
        return getFluidData(pos).getColor();
    }
}
