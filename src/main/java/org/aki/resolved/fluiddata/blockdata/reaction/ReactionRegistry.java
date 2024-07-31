package org.aki.resolved.fluiddata.blockdata.reaction;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import org.aki.resolved.fluiddata.blockdata.ListHelper;

import java.util.LinkedList;
import java.util.ListIterator;

public class ReactionRegistry {

    private final Int2ObjectOpenHashMap<Reaction> registry;
    private final Int2ObjectOpenHashMap<LinkedList<Reaction>> searchList;
    public static final ReactionRegistry REGISTRY = new ReactionRegistry();

    private ReactionRegistry() {
        registry = new Int2ObjectOpenHashMap<>();
        searchList = new Int2ObjectOpenHashMap<>();
    }

    public int register(Reaction reaction) {
        int newKey = registry.size() + 1;
        registry.put(newKey, reaction);
        for (IntIterator it = reaction.getReagentIterator(); it.hasNext();) {
            int consId = it.nextInt();
            if (!searchList.containsKey(consId)) {
                searchList.get(consId).add(reaction);
            } else {
                LinkedList<Reaction> list = new LinkedList<>();
                searchList.put(consId, list);
                list.add(reaction);
            }
        }
        return newKey;
    }

    public Reaction get(int key) {
        return registry.get(key);
    }

    public ListIterator<Reaction> getCandidateIterator(int consId) {
        return searchList.containsKey(consId) ? searchList.get(consId).listIterator() : new ListHelper.NullListIterator<>();
    }

}
