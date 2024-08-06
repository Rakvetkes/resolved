package org.aki.resolved.reaction;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntConsumer;

public final class ReactionRegistry<T extends Reaction> {

    private final Int2ObjectOpenHashMap<T> registry;
    private final Int2ObjectOpenHashMap<IntArrayList> searchList;
    public static final ReactionRegistry<InnerReaction> INNER_REACTION_REGISTRY = new ReactionRegistry<>();
    public static final ReactionRegistry<SurfaceReaction> SURFACE_REACTION_REGISTRY = new ReactionRegistry<>();

    private ReactionRegistry() {
        registry = new Int2ObjectOpenHashMap<>();
        searchList = new Int2ObjectOpenHashMap<>();
    }

    public int register(T reaction) {
        int newKey = registry.size() + 1;
        registry.put(newKey, reaction);
        reaction.forEachReagent(value -> {
            if (searchList.containsKey(value)) {
                searchList.get(value).add(newKey);
            } else {
                IntArrayList list = new IntArrayList();
                searchList.put(value, list);
                list.add(newKey);
            }
        });
        return newKey;
    }

    public T get(int key) {
        return registry.get(key);
    }

    public void forEachCandidate(int consId, IntConsumer consumer) {
        if (searchList.containsKey(consId)) {
            for (int candidate : searchList.get(consId)) {
                consumer.accept(candidate);
            }
        }
    }

}
