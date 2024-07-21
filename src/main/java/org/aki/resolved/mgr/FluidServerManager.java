package org.aki.resolved.mgr;

import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class FluidServerManager implements FluidDataAccessor {

    private ServerWorld world;
    private HashMap<ChunkPos, FluidChunkData> cdatas;

    public FluidServerManager(ServerWorld world) {
        this.world = world;
        cdatas = new HashMap<>();
    }

    public void mark(BlockPos pos) {

    }

    @Deprecated
    public int getColor(BlockPos pos) {
        return 0x4040FF;    // todo
    }

    public void tick() {

    }

    public void onChunkLoading(Chunk chunk) {
        Log.debug(LogCategory.GENERAL, "loading chunk fluid data: " +chunk.getPos());
        cdatas.put(chunk.getPos(), FluidChunkDataRegistry.FLUID_DATA.get(chunk));
    }
    public void onChunkUnloading(Chunk chunk) {
        cdatas.remove(chunk.getPos());
    }

    @Override
    public @NotNull FluidBlockContent get(BlockPos pos) {
        FluidChunkData ck = cdatas.get(new ChunkPos(pos));
        if (ck != null) {
            return ck.get(pos);
        }
        return  new FluidBlockContent();
    }
}
