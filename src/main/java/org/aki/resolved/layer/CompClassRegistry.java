package org.aki.resolved.layer;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public final class CompClassRegistry {

    private final Int2IntOpenHashMap registry;
    private int classCount;
    public static final CompClassRegistry REGISTRY = new CompClassRegistry();

    private CompClassRegistry() {
        registry = new Int2IntOpenHashMap();
        classCount = 0;
    }

    public void createClass(int constituent) {
        registry.put(constituent, ++classCount);
    }

    public void register(int constituent, int compatible) {
        if (!registry.containsKey(compatible)) {
            throw new IllegalArgumentException();
        }
        registry.put(constituent, registry.get(compatible));
    }

    public boolean checkCompatibility(int constituentA, int constituentB) {
        return registry.get(constituentA) == registry.get(constituentB);
    }

}
