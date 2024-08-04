package org.aki.resolved.misc;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.aki.resolved.Registered;

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
        return 0;
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
}