package org.aki.resolved.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ResolvedFluidRenderer extends SimpleFluidRenderHandler {

    public ResolvedFluidRenderer() {
        super(WATER_STILL, WATER_FLOWING, WATER_OVERLAY);
    }

    @Override
    public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        throw new UnsupportedOperationException();
//        if (view == null) return 0x0;
//        Chunk chunk = getWorld(view).getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
//        return LayerSetHelper.getColor(Registered.FLUID_DATA.get(chunk).getFluidData(pos.getX() & 15, pos.getY(), pos.getZ() & 15));
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

    private int getLight(BlockRenderView world, BlockPos pos) {
        int i = WorldRenderer.getLightmapCoordinates(world, pos);
        int j = WorldRenderer.getLightmapCoordinates(world, pos.up());
        int k = i & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xF);
        int l = j & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xF);
        int m = i >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xF);
        int n = j >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xF);
        return (Math.max(k, l)) | (Math.max(m, n)) << 16;
    }

//    private void vertex(VertexConsumer vertexConsumer, float f, float g, float h, float l, float m) {
//        vertexConsumer.vertex(f, g, h).color(0.0f, 0.8f, 0f, 1f).texture(l, m).light(15728640).normal(0, 1.0f, 0);
//    }
    void drawSquare(VertexConsumer vertexConsumer, int light, float x, float y, float z, float d1, float d2, int argb, Direction direction) {
        float ul = sprites[0].getFrameU(0), ur = sprites[0].getFrameU(d1);
        float vl = sprites[0].getFrameV(0), vr = sprites[0].getFrameV(d2);
        switch (direction) {
            case DOWN -> {
                vertexConsumer.vertex(x, y, z).color(argb).texture(ul, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y, z + d2).color(argb).texture(ul, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y, z + d2).color(argb).texture(ur, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y, z).color(argb).texture(ur, vl).light(light).normal(0, 1f, 0);
            }
            case UP -> {
                vertexConsumer.vertex(x, y, z).color(argb).texture(ul, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y, z).color(argb).texture(ur, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y, z + d2).color(argb).texture(ur, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y, z + d2).color(argb).texture(ul, vr).light(light).normal(0, 1f, 0);
            }
            case NORTH -> {
                vertexConsumer.vertex(x, y, z).color(argb).texture(ul, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y + d2, z).color(argb).texture(ul, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y + d2, z).color(argb).texture(ur, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y, z).color(argb).texture(ur, vl).light(light).normal(0, 1f, 0);
            }
            case SOUTH -> {
                vertexConsumer.vertex(x, y, z).color(argb).texture(ul, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y, z).color(argb).texture(ur, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y + d2, z).color(argb).texture(ur, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y + d2, z).color(argb).texture(ul, vr).light(light).normal(0, 1f, 0);
            }
            case WEST -> {
                vertexConsumer.vertex(x, y, z).color(argb).texture(ul, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y, z + d1).color(argb).texture(ul, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y + d2, z + d1).color(argb).texture(ur, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y + d2, z).color(argb).texture(ur, vl).light(light).normal(0, 1f, 0);
            }
            case EAST -> {
                vertexConsumer.vertex(x, y, z).color(argb).texture(ul, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y + d2, z).color(argb).texture(ur, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y + d2, z + d1).color(argb).texture(ur, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y, z + d1).color(argb).texture(ul, vr).light(light).normal(0, 1f, 0);
            }
        };
    }
    void drawCubeWall(VertexConsumer vertexConsumer, int light, float x, float y, float z, float height, int argb) {
        x += 0.001f;
        y += 0.001f;
        drawSquare(vertexConsumer, light, x, y, z, 0.998f, height, argb, Direction.WEST);
        drawSquare(vertexConsumer, light, x, y, z, 0.998f, height, argb, Direction.EAST);
        drawSquare(vertexConsumer, light, x, y, z, 0.998f, height, argb, Direction.NORTH);
        drawSquare(vertexConsumer, light, x, y, z, 0.998f, height, argb, Direction.SOUTH);
    }
    @Override
    public void renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {

//        super.renderFluid(pos, world, vertexConsumer, blockState, fluidState);


        int x = pos.getX() & 0xF, y = pos.getY() &0xF, z = pos.getZ() & 0xF;
        drawCubeWall(vertexConsumer, getLight(world, pos), x, y, z, 0.999f, Colors.GREEN);
//        drawSquare(vertexConsumer, 15728640, x, y, z, 1f, 1f, Colors.GREEN, Direction.DOWN);
//        vertex(vertexConsumer, x, y, z, ul, vl);
//        vertex(vertexConsumer, x + 1f, y, z, ur, vl);
//        vertex(vertexConsumer, x + 1f, y, z + 1f, ur, vr);
//        vertex(vertexConsumer, x, y, z + 1f, ul, vr);
        // todo
    }

}