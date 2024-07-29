package org.aki.resolved.fluidblock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.aki.resolved.fluiddata.FluidChunkDataRegistry;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ResolvedFluidRenderer extends SimpleFluidRenderHandler {

    public ResolvedFluidRenderer() {
        super(WATER_STILL, WATER_FLOWING, WATER_OVERLAY);
    }

    @Override
    public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        if (view == null) return 0x0;
        Chunk chunk = getWorld(view).getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
        return FluidChunkDataRegistry.FLUID_DATA.get(chunk).getFluidData(pos.getX() & 15, pos.getY(), pos.getZ() & 15).getColor();
    }

    public static World getWorld(BlockRenderView view) {
        if (view.getClass().equals(ChunkRendererRegion.class)) {
            try {
                return (World) view.getClass().getDeclaredField("world").get(view);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException();
    }

}