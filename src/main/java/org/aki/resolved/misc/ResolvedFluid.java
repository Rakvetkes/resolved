package org.aki.resolved.misc;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.aki.resolved.Registered;
import org.aki.resolved.chunk.FluidChunk;
import org.aki.resolved.layer.FluidLayerSet;
import org.aki.resolved.reaction.ReactionRegistry;
import org.aki.resolved.reaction.SurfaceReaction;

public class ResolvedFluid extends Fluid {

    @Override
    protected Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state) {
        return new Vec3d(0, 0, 0);
    }

    @Override
    public float getHeight(FluidState state, BlockView world, BlockPos pos) {
        return 1.0f;
    }

    @Override
    public float getHeight(FluidState state) {
        return 1.0f;
    }

    @Override
    public VoxelShape getShape(FluidState state, BlockView world, BlockPos pos) {
        return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return Registered.RESOLVED_FLUID_BLOCK.getDefaultState();
    }

    @Override
    public Item getBucketItem() {
        return null;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 200;
    }

    @Override
    protected float getBlastResistance() {
        return 0;
    }

    @Override
    public boolean isStill(FluidState state) {
        return false;
    }

    @Override
    public int getLevel(FluidState state) {
        return 0;
    }

    private FluidLayerSet getFluidData(World world, BlockPos pos) {
        return Registered.FLUID_DATA.get(world.getChunk(pos.getX() >> 4, pos.getZ() >> 4))
                .getFluidData(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
    }

    private void setFluidData(World world, BlockPos pos, FluidLayerSet data) {
        Registered.FLUID_DATA.get(world.getChunk(pos.getX() >> 4, pos.getZ() >> 4))
                .setFluidData(pos.getX() & 15, pos.getY(), pos.getZ() & 15, data);
    }

    private void updateBlockState(World world, BlockPos pos, FluidLayerSet data) {
        if (data.isAir()) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        } else {
            final boolean[] flag = new boolean[1];
            data.forEachLayer(layer -> flag[0] |= layer.isSolid());
            if (!flag[0]) world.setBlockState(pos, Registered.RESOLVED_FLUID_BLOCK.getDefaultState());
            // todo modifying block states for waterlogged blocks
        }
    }

    @Override
    public void onScheduledTick(World world, BlockPos pos, FluidState state) {
        FluidChunk chunk = Registered.FLUID_DATA.get(world.getChunk(pos.getX() >> 4, pos.getZ() >> 4));
        FluidLayerSet a = getFluidData(world, pos), b = a.getMutable();
        float temp = 114.0f;
        for (Direction r : Direction.values()) {
            if (pos.offset(r).getY() <= world.getTopY() && pos.offset(r).getY() >= world.getBottomY()) {
                FluidLayerSet c = getFluidData(world, pos.offset(r)), d = c.getMutable();
                if (r == Direction.UP || r == Direction.DOWN) {
                    b.exchangeVertical(d, r == Direction.UP);
                    chunk.forEachSurfaceReaction(value -> {
                        SurfaceReaction o = ReactionRegistry.SURFACE_REACTION_REGISTRY.get(value);
                        if (r == Direction.UP) o.react(b.getTopLayerMutable(), d.getBottomLayerMutable(), temp);
                        else o.react(d.getTopLayerMutable(), b.getBottomLayerMutable(), temp);
                    });
                } else {
                    FluidLayerSet.exchange(b, d, 0.2f);
                }
                if (!c.equals(d)) {
                    updateBlockState(world, pos.offset(r), d);
                    setFluidData(world, pos.offset(r), d);
                    world.scheduleFluidTick(pos.offset(r), this, getTickRate(world));
                }
            }
        }
        b.forEachLayer(l -> chunk.forEachInnerReaction(v -> ReactionRegistry.INNER_REACTION_REGISTRY.get(v).react(l, temp)));
        if (!a.equals(b)) {
            updateBlockState(world, pos, b);
            setFluidData(world, pos, b);
            world.scheduleFluidTick(pos, this, getTickRate(world));
        }
    }

}
