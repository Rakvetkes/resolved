package org.aki.resolved.render;

import it.unimi.dsi.fastutil.floats.FloatFloatImmutablePair;
import it.unimi.dsi.fastutil.ints.IntFloatImmutablePair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.aki.resolved.Registered;
import org.aki.resolved.layer.FluidLayerSet;
import org.aki.resolved.reaction.RangeHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ResolvedFluidRenderer extends SimpleFluidRenderHandler {

    public ResolvedFluidRenderer() {
        super(WATER_STILL, WATER_FLOWING, WATER_OVERLAY);
    }

    @Override
    public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        throw new UnsupportedOperationException();
    }

    protected int getLight(BlockRenderView world, BlockPos pos) {
        int i = WorldRenderer.getLightmapCoordinates(world, pos);
        int j = WorldRenderer.getLightmapCoordinates(world, pos.up());
        int k = i & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xF);
        int l = j & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xF);
        int m = i >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xF);
        int n = j >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xF);
        return (Math.max(k, l)) | (Math.max(m, n)) << 16;
    }

    protected static float getHeight(BlockRenderView world, BlockPos pos) {
        FluidLayerSet data = Registered.FLUID_DATA.get(BlockViewHelper.getWorld(world).getChunk(pos.getX() >> 4, pos.getZ() >> 4))
                .getFluidData(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
        return 1.0f - (data.getTopLayer().isAir() ? data.getTopLayer().getVolume() / FluidLayerSet.FULL_VOLUME : 0.0f);
    }

    protected static FloatFloatImmutablePair shouldRenderHeight(BlockRenderView world, BlockPos pos, Direction direction) {
        // todo this is only for test
//        return FloatFloatImmutablePair.of(0, 1);
        // todo this is what should they do
        if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) {
            throw new UnsupportedOperationException("Direction should be vertical.");
        }
        float a = getHeight(world, pos), b = getHeight(world, pos.add(direction.getVector()));
        return a > b ? FloatFloatImmutablePair.of(b, a) : FloatFloatImmutablePair.of(0, 0);
    }

    /**
     * @param light Light for OpenGL
     * @param d2 size of square, which usually be vertical.
     * @param argb color
     * @param direction From where you can see
     */
    void drawSquare(VertexConsumer vertexConsumer, int light, float x, float y, float z, float d1, float d2, int argb, @NotNull Direction direction, int textureId) {
        float ul, ur, vl, vr;
        if (textureId == -1) {
            ul = sprites[0].getFrameU(0);
            vl = sprites[0].getFrameV(0);
            ur = sprites[0].getFrameU(1f/16);
            vr = sprites[0].getFrameV(1f/16);
        } else {
            ul = sprites[textureId].getFrameU(0);
            vl = sprites[textureId].getFrameV(0);
            ur = sprites[textureId].getFrameU(d1);
            vr = sprites[textureId].getFrameV(d2);
        }
        switch (direction) {
            case UP -> {
                vertexConsumer.vertex(x, y, z).color(argb).texture(ul, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y, z + d2).color(argb).texture(ul, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y, z + d2).color(argb).texture(ur, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y, z).color(argb).texture(ur, vl).light(light).normal(0, 1f, 0);
            }
            case DOWN -> {
                vertexConsumer.vertex(x, y, z).color(argb).texture(ul, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y, z).color(argb).texture(ur, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y, z + d2).color(argb).texture(ur, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y, z + d2).color(argb).texture(ul, vr).light(light).normal(0, 1f, 0);
            }
            case NORTH -> {
                vertexConsumer.vertex(x, y, z).color(argb).texture(ur, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y + d2, z).color(argb).texture(ur, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y + d2, z).color(argb).texture(ul, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y, z).color(argb).texture(ul, vr).light(light).normal(0, 1f, 0);
            }
            case SOUTH -> {
                vertexConsumer.vertex(x, y, z).color(argb).texture(ur, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y, z).color(argb).texture(ul, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x + d1, y + d2, z).color(argb).texture(ul, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y + d2, z).color(argb).texture(ur, vl).light(light).normal(0, 1f, 0);
            }
            case WEST -> {
                vertexConsumer.vertex(x, y, z).color(argb).light(light).texture(ul, vr).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y, z + d1).color(argb).texture(ur, vr).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y + d2, z + d1).color(argb).texture(ur, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y + d2, z).color(argb).texture(ul, vl).light(light).normal(0, 1f, 0);
            }
            case EAST -> {
                vertexConsumer.vertex(x, y, z).color(argb).light(light).texture(ul, vr).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y + d2, z).color(argb).texture(ul, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y + d2, z + d1).color(argb).texture(ur, vl).light(light).normal(0, 1f, 0);
                vertexConsumer.vertex(x, y, z + d1).color(argb).texture(ur, vr).light(light).normal(0, 1f, 0);
            }
        }
    }

    void drawSquare(VertexConsumer vertexConsumer, int light, float x, float y, float z, float d1, float d2, int argb, @NotNull Direction direction) {
        drawSquare(vertexConsumer, light, x, y, z, d1, d2, argb, direction, 0);
    }

    IntFloatImmutablePair[] getLayerColors(BlockRenderView world, BlockPos pos) {
        // todo
        var t = new IntFloatImmutablePair[1];
        t[0] = IntFloatImmutablePair.of(Colors.LIGHT_YELLOW, getHeight(world, pos));
        return  t;
    }
    boolean shouldRenderBottom(BlockRenderView world, BlockPos pos) {
        // todo
        return true;
    }
    boolean shouldRenderTop(BlockRenderView world, BlockPos pos) {
        // todo
        return true;
    }
    @Override
    public void renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
        int x = pos.getX() & 0xF, y = pos.getY() &0xF, z = pos.getZ() & 0xF;
        final IntFloatImmutablePair[] layers = getLayerColors(world, pos);
        if (layers.length == 0) {
            return;
        }
        float fx = x + 0.001f, fy = y + 0.001f, fz = z + 0.001f;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            final FloatFloatImmutablePair renderRange = shouldRenderHeight(world, pos, direction);
            float last = 0f;
            for (IntFloatImmutablePair i : layers) {
                var commonRange = RangeHelper.getIntersection(renderRange, FloatFloatImmutablePair.of(last, last + i.rightFloat()));
                if (RangeHelper.isEmpty(commonRange))
                    continue;
                float xd = fx, zd = fz;
                if (direction == Direction.SOUTH) {
                    zd += 0.998f;
                } else if (direction == Direction.EAST) {
                    xd += 0.998f;
                }
                drawSquare(vertexConsumer, getLight(world, pos), xd, fy + commonRange.leftFloat(), zd, 0.998f, RangeHelper.getLength(commonRange), i.leftInt(), direction, 1);
                last += i.rightFloat();
            }
        }
        float lasth = layers[0].rightFloat();
        for (int i = 1; i < layers.length; ++i) {
            drawSquare(vertexConsumer, getLight(world, pos), fx, y + lasth, fy, 0.998f, 0.998f, layers[i - 1].leftInt(), Direction.UP);
            drawSquare(vertexConsumer, getLight(world, pos), fx, y + lasth, fy, 0.998f, 0.998f, layers[i].leftInt(), Direction.DOWN);
            lasth += layers[i].rightFloat();
        }
        float height = 0f;
        for (IntFloatImmutablePair i : layers)
            height += i.rightFloat();
        // todo Here should adjust the color they show.
        if (shouldRenderBottom(world, pos)) {
            drawSquare(vertexConsumer, getLight(world, pos), fx, fy, fz,0.998f, 0.998f, layers[0].leftInt(), Direction.DOWN);
            drawSquare(vertexConsumer, getLight(world, pos), fx, fy, fz,0.998f, 0.998f, layers[0].leftInt(), Direction.UP);
        }
        if (shouldRenderTop(world, pos)) {
            drawSquare(vertexConsumer, getLight(world, pos), fx, fy + height * 0.998f, fz, 0.998f, 0.998f, layers[layers.length - 1].leftInt(), Direction.UP);
            drawSquare(vertexConsumer, getLight(world, pos), fx, fy + height * 0.998f, fz, 0.998f, 0.998f, layers[layers.length - 1].leftInt(), Direction.DOWN);
        }
    }

}