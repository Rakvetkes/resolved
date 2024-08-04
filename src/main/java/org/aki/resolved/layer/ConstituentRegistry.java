package org.aki.resolved.layer;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.collection.Int2ObjectBiMap;

public class ConstituentRegistry {

    private final Int2ObjectBiMap<Object> registry;
    private final Int2ObjectOpenHashMap<ConstituentAttributes> attributeRegistry;
    public static final ConstituentRegistry REGISTRY = new ConstituentRegistry();

    private ConstituentRegistry() {
        registry = Int2ObjectBiMap.create(1);
        attributeRegistry = new Int2ObjectOpenHashMap<>();
    }

    public int register(Object constituent, ConstituentAttributes attributes) {
        if (!registry.contains(constituent)) {
            int newKey = registry.size();
            registry.put(constituent, newKey);
            attributeRegistry.put(newKey, attributes);
            return newKey;
        } else {
            return registry.getRawId(constituent);
        }
    }

    public int count() {
        return registry.size();
    }

    public Object get(int index) {
        return registry.get(index);
    }

    public ConstituentAttributes getAttributes(int index) {
        return attributeRegistry.get(index);
    }

    public int get(Object constituent) {
        return registry.getRawId(constituent);
    }

    public record ConstituentAttributes(float volume, float density, float energy) {

        public ConstituentAttributes volume(float volume) {
            return new ConstituentAttributes(volume, density, energy);
        }

        public ConstituentAttributes density(float density) {
            return new ConstituentAttributes(volume, density, energy);
        }

        public ConstituentAttributes energy(float energy) {
            return new ConstituentAttributes(volume, density, energy);
        }

    }

}
