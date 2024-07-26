package org.aki.resolved.manager;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public interface FluidDataAccessor {
    @NotNull
    FluidBlockData getFluidContent(BlockPos pos);
}
