package org.aki.resolved.mgr;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashMap;

public class FluidManagerRegistry {

    static private HashMap<Identifier, FluidServerManager> registry;
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
    public static void registerForServer() {
        ServerChunkEvents.CHUNK_LOAD.register(((world, chunk) -> {
            FluidServerManager fm = REGISTRY.get(world);
            if (fm != null) {
                fm.onChunkLoading(chunk);
            }
        }));
        ServerChunkEvents.CHUNK_UNLOAD.register(((world, chunk) -> {
            FluidServerManager fm = REGISTRY.get(world);
            if (fm != null) {
                fm.onChunkUnloading(chunk);
            }
        }));
    }
}
