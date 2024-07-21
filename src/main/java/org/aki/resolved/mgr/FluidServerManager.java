package org.aki.resolved.mgr;

import net.minecraft.block.FluidBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;

public class FluidServerManager implements FluidDataAccessor {

    private ServerWorld world;
    private NbtCompound nbt;
    HashMap<ChunkPos, FluidChunkData> cdatas;

    public FluidServerManager() {
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

    static public FluidServerManager load(ServerWorld world) {
        System.out.println("[Resolved] Loading");
        FluidServerManager fluidManager = new FluidServerManager();
        fluidManager.world = world;
        return fluidManager;
    }

    public void onChunkLoading(Chunk chunk) {
        System.out.println("loading chunk fluid data: " +chunk.getPos());
        cdatas.put(chunk.getPos(), FluidChunkDataRegistry.FLUID_DATA.get(chunk));
    }
    public void onChunkUnloading(Chunk chunk) {
        cdatas.remove(chunk.getPos());
    }

    static public void save(Path directory, FluidServerManager fluidManager) {
        System.out.println("[Resolved] Saving in " + directory);
        // todo
        // deprecated
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
