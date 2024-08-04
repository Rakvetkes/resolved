package org.aki.resolved.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.aki.resolved.Registered;
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
        return LayerSetHelper.getColor(Registered.FLUID_DATA.get(chunk).getFluidData(pos.getX() & 15, pos.getY(), pos.getZ() & 15));
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

    private void vertex(VertexConsumer vertexConsumer, float f, float g, float h, float l, float m) {
        vertexConsumer.vertex(f, g, h).color(0.0f, 0.8f, 0f, 1f).texture(l, m).light(15728640).normal(0, 1.0f, 0);
    }
    @Override
    public void renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {

//        super.renderFluid(pos, world, vertexConsumer, blockState, fluidState);


        int x = pos.getX() & 0xF, y = pos.getY() &0xF, z =pos.getZ() & 0xF;
        float ul = sprites[0].getFrameU(0), ur = sprites[0].getFrameU(1f);
        float vl = sprites[0].getFrameV(0), vr = sprites[0].getFrameV(1f);
        vertex(vertexConsumer, x, y, z, ul, vl);
        vertex(vertexConsumer, x + 0.999f, y, z, ur, vl);
        vertex(vertexConsumer, x + 0.999f, y, z + 0.999f, ur, vr);
        vertex(vertexConsumer, x, y, z + 0.999f, ul, vr);
        // todo
    }

}