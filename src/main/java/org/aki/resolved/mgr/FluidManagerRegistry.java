package org.aki.resolved.mgr;

import net.minecraft.util.Identifier;

import java.util.HashMap;

public class FluidManagerRegistry {

    static private HashMap<Identifier, FluidManager> registry;
    static final public FluidManagerRegistry REGISTRY = new FluidManagerRegistry();

    private FluidManagerRegistry() {
        registry = new HashMap<>();
    }

    public void register(Identifier identifier, FluidManager fluidManager) {
        registry.put(identifier, fluidManager);
    }

    public FluidManager get(Identifier identifier) {
        return registry.get(identifier);
    }

}
