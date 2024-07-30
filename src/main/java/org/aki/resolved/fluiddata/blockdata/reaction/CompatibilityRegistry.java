package org.aki.resolved.fluiddata.blockdata.reaction;

import net.minecraft.util.collection.Int2ObjectBiMap;

public class CompatibilityRegistry {

    private final Int2ObjectBiMap<Object> registry;
    private int classCount;
    public static final CompatibilityRegistry REGISTRY = new CompatibilityRegistry();

    private CompatibilityRegistry() {
        registry = Int2ObjectBiMap.create(1);
        classCount = 0;
    }

    public void createClass(Object constituent) {
        registry.put(constituent, ++classCount);
    }

    public void register(Object constituent, Object compatible) {
        if (!registry.contains(compatible)) {
            throw new IllegalArgumentException();
        }
        registry.put(constituent, registry.getRawId(compatible));
    }

    public boolean checkCompatibility(Object constituentA, Object constituentB) {
        return registry.getRawId(constituentA) == registry.getRawId(constituentB);
    }

}
