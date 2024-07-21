package org.aki.resolved.fluidblock;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.aki.resolved.common.PublicVars;
import org.aki.resolved.mgr.FluidServerManager;
import org.aki.resolved.mgr.FluidManagerRegistry;

public class ResolvedFluid extends FlowableFluid {

    /* Abandoned vanilla features */

    @Deprecated @Override
    public Fluid getFlowing() {
        return this;
    }

    @Deprecated @Override
    public Fluid getStill() {
        return this;
    }

    @Deprecated @Override
    public boolean isStill(FluidState state) {
        return true;
    }

    @Deprecated @Override
    protected int getMaxFlowDistance(WorldView world) {
        return 4;
    }

    @Deprecated @Override
    protected boolean isInfinite(World world) {
        return false;
    }

    @Deprecated @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return 1;
    }

    @Deprecated @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {

    }

    /* Unused but reserved vanilla features */

    @Deprecated @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 1;
    }

    @Override
    protected float getBlastResistance() {
        return 100.0F;
    }


    public ResolvedFluid() {
        setDefaultState(getDefaultState().with(LEVEL, 8));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
        super.appendProperties(builder);
        builder.add(LEVEL);
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return PublicVars.RESOLVED_FLUID_BLOCK.getDefaultState()
                .with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    @Override
    public int getLevel(FluidState state) {
        return state.get(LEVEL);
    }


    @Override
    public Item getBucketItem() {
        return null;        // todo
    }

    @Override
    public void onScheduledTick(World world, BlockPos pos, FluidState state) {
        FluidServerManager manager = FluidManagerRegistry.REGISTRY.get(world.getRegistryKey().getValue());
        manager.mark(pos);
    }

}
