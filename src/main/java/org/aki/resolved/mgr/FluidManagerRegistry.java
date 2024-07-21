package org.aki.resolved.mgr;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;

public class FluidManagerRegistry {

    private HashMap<Identifier, FluidServerManager> registry;
    static final public FluidManagerRegistry REGISTRY = new FluidManagerRegistry();

    private FluidManagerRegistry() {
        registry = new HashMap<>();
    }

    public void register(Identifier id, FluidServerManager fluidManager) {
        registry.put(id, fluidManager);
    }



    public FluidServerManager get(World world) {
        return registry.get(world.getRegistryKey().getValue());
    }
    public FluidServerManager get(Identifier id) {
        return registry.get(id);
    }

}
