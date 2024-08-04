package org.aki.resolved.reaction;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.aki.resolved.layer.ListHelper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class ReactionRegistry<T extends Reaction> {

    private final Int2ObjectOpenHashMap<T> registry;
    private final Int2ObjectOpenHashMap<LinkedList<T>> searchList;
    public static final ReactionRegistry<InnerReaction> INNER_REACTION_REGISTRY = new ReactionRegistry<>();
    public static final ReactionRegistry<SurfaceReaction> SURFACE_REACTION_REGISTRY = new ReactionRegistry<>();

    private ReactionRegistry() {
        registry = new Int2ObjectOpenHashMap<>();
        searchList = new Int2ObjectOpenHashMap<>();
    }

    public int register(T reaction) {
        int newKey = registry.size() + 1;
        registry.put(newKey, reaction);
        for (Iterator<Integer> it = reaction.getReagentIterator(); it.hasNext();) {
            int consId = it.next();
            if (searchList.containsKey(consId)) {
                searchList.get(consId).add(reaction);
            } else {
                LinkedList<T> list = new LinkedList<>();
                searchList.put(consId, list);
                list.add(reaction);
            }
        }
        return newKey;
    }

    public T get(int key) {
        return registry.get(key);
    }

    public ListIterator<T> getCandidateIterator(int consId) {
        return searchList.containsKey(consId) ? searchList.get(consId).listIterator()
                : new ListHelper.NullListIterator<>();
    }

}
