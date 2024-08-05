package org.aki.resolved.layer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class StateModelRegistry {

    private final Object2ObjectOpenHashMap<Block, ModelProvider> registry;
    public static final StateModelRegistry REGISTRY = new StateModelRegistry();

    private StateModelRegistry() {
        registry = new Object2ObjectOpenHashMap<>();
    }

    public void register(Block block, ModelProvider data) {
        registry.put(block, data);
    }

    public boolean containsKey(Block block) {
        return registry.containsKey(block);
    }

    public ModelProvider get(Block block) {
        return registry.get(block);
    }

    public interface ModelProvider {
        FluidLayerSet getModel(BlockState blockState);
    }

}
