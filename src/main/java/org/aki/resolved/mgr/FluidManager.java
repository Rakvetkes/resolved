package org.aki.resolved.mgr;

import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.nio.file.Path;

public class FluidManager {

    private World world;
    private NbtCompound nbt;

    public FluidManager() {

    }

    public void mark(BlockPos pos) {

    }

    @Deprecated
    public int getColor(BlockPos pos) {
        return 0x4040FF;    // todo
    }

    public void tick() {

    }

    static public FluidManager load(Path directory, World world) {
        System.out.println("[Resolved] Loading from " + directory);
        FluidManager fluidManager = new FluidManager();
        fluidManager.world = world;
        return fluidManager;        // todo
    }

    static public void save(Path directory, FluidManager fluidManager) {
        System.out.println("[Resolved] Saving in " + directory);
        // todo
    }

}
