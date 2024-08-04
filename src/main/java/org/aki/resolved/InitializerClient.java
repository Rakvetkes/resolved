package org.aki.resolved;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.render.RenderLayer;
import org.aki.resolved.render.ResolvedFluidRenderer;

@Environment(EnvType.CLIENT)
public class InitializerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putFluid(Registered.RESOLVED_FLUID, RenderLayer.getTranslucent());
        FluidRenderHandlerRegistry.INSTANCE.register(Registered.RESOLVED_FLUID, new ResolvedFluidRenderer());
    }

}
