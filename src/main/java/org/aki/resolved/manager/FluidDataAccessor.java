package org.aki.resolved.mgr;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public interface FluidDataAccessor {
    public @NotNull FluidBlockContent get(BlockPos pos);
}
