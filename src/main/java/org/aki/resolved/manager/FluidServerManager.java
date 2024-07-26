package org.aki.resolved.manager;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

public class FluidServerManager implements FluidDataAccessor {

    private final ServerWorld world;
//    private final HashMap<ChunkPos, FluidChunkData> cdatas;

    public FluidServerManager(ServerWorld world) {
        this.world = world;
        // cdatas = new HashMap<>();
    }

//    public void tick() {
//
//    }

//    public void onChunkLoading(Chunk chunk) {
//        Log.debug(LogCategory.GENERAL, "loading chunk fluid data: " +chunk.getPos());
//        cdatas.put(chunk.getPos(), FluidChunkDataRegistry.FLUID_DATA.get(chunk));
//    }
//    public void onChunkUnloading(Chunk chunk) {
//        cdatas.remove(chunk.getPos());
//    }

    public FluidChunkData getChunk(ChunkPos chunkPos) {
        Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
        if (chunk == null) throw new IllegalStateException();
        return FluidChunkDataRegistry.FLUID_DATA.get(chunk);
    }

    @Override
    public @NotNull FluidBlockData getFluidContent(BlockPos pos) {
        FluidChunkData chunk = getChunk(new ChunkPos(pos));
        return chunk.get(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
    }

    public int getColor(BlockPos pos) {
        return getFluidContent(pos).getColor();
    }
}
