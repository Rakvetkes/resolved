package org.aki.resolved.fluidblock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.aki.resolved.manager.FluidServerManager;
import org.aki.resolved.manager.FluidManagerRegistry;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ResolvedFluidRenderer extends SimpleFluidRenderHandler {

    public ResolvedFluidRenderer() {
        super(WATER_STILL, WATER_FLOWING, WATER_OVERLAY);
    }

    @Override
    public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        if (view == null) return 0x0;
        FluidServerManager manager = FluidManagerRegistry.REGISTRY.get((getWorld(view)).getRegistryKey().getValue());
        return manager.getColor(pos);
    }

    public World getWorld(BlockRenderView view) {
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