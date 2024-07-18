package org.aki.resolved;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.render.RenderLayer;
import org.aki.resolved.common.PublicVars;
import org.aki.resolved.fluidblock.ResolvedFluidRenderer;

public class ResolvedClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putFluid(PublicVars.RESOLVED_FLUID, RenderLayer.getTranslucent());
        FluidRenderHandlerRegistry.INSTANCE.register(PublicVars.RESOLVED_FLUID, new ResolvedFluidRenderer());
    }

}
