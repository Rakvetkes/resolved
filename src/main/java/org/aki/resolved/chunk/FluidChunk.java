package org.aki.resolved.chunk;

import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.chunk.Chunk;
import org.aki.resolved.Registered;
import org.aki.resolved.layer.FluidLayerSet;
import org.aki.resolved.reaction.InnerReaction;
import org.aki.resolved.reaction.Reaction;
import org.aki.resolved.reaction.ReactionRegistry;
import org.aki.resolved.reaction.SurfaceReaction;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.LinkedList;
import java.util.List;

public class FluidChunk implements Component, AutoSyncedComponent {

    private static final int SECTION_SIZE = 1 << 4 * 3;
    private final PaletteContainer<FluidLayerSet>[] sectionsData;
    private final ActiveReactionRegistry<InnerReaction> activeInnerReactions;
    private final ActiveReactionRegistry<SurfaceReaction> activeSurfaceReactions;
    private Int2IntLinkedOpenHashMap existingConstituents;              // defRetValue is supposed to be 0
    private final int bottomY;

    public static final List<FluidChunk> SYNC_QUEUE = new LinkedList<>();
    // todo save the chunks need to be in sync with client.

    public FluidChunk(Chunk chunk) {
        sectionsData = new PaletteContainer[chunk.countVerticalSections()];
        activeInnerReactions = new ActiveReactionRegistry<>(ReactionRegistry.INNER_REACTION_REGISTRY);
        activeSurfaceReactions = new ActiveReactionRegistry<>(ReactionRegistry.SURFACE_REACTION_REGISTRY);
        existingConstituents = new Int2IntLinkedOpenHashMap();
        existingConstituents.put(Registered.CONSTITUENT_AIR, SECTION_SIZE);     // fill the counter with air
        bottomY = chunk.getBottomY();
        for (int i = 0; i < sectionsData.length; ++i) {
            sectionsData[i] = new PaletteContainer<>(SECTION_SIZE, FluidLayerSet.NULL_LAYER_SET, SimpleConverter.INSTANCE);
        }
    }

    private int computeIndex(int i, int j, int k) {
        return (i << 4 * 2) | (j << 4) | k;
    }

    public FluidLayerSet getFluidData(int i, int j, int k) {
        return sectionsData[(j - bottomY) >> 4].get(computeIndex(i, (j - bottomY) & 15, k)).getImmutable();
    }

    private void markConstituents(FluidLayerSet layerSet, int marker) {
        layerSet.forEachLayer(layer -> layer.forEachConstituent((integer, aFloat) -> {
            existingConstituents.addTo(integer, marker);
            if (marker < 0) {
                activeInnerReactions.checkForInactivation(integer);
                activeSurfaceReactions.checkForInactivation(integer);
            } else {
                activeInnerReactions.checkForActivation(integer);
                activeSurfaceReactions.checkForActivation(integer);
            }
        }));
    }

    public void setFluidData(int i, int j, int k, FluidLayerSet data) {
        int sectionId = (j - bottomY) >> 4;
        int index = computeIndex(i, (j - bottomY) & 15, k);
        markConstituents(sectionsData[sectionId].get(index), -1);
        markConstituents(data, 1);
        sectionsData[sectionId].set(index, data);
    }

    public void forEachInnerReaction(IntConsumer consumer) {
        activeInnerReactions.forEachReaction(consumer);
    }

    public void forEachSurfaceReaction(IntConsumer consumer) {
        activeSurfaceReactions.forEachReaction(consumer);
    }

    private class ActiveReactionRegistry<T extends Reaction> {

        private final IntRBTreeSet registry;
        private final Int2ObjectRBTreeMap<IntRBTreeSet> searchList;
        private final ReactionRegistry<T> linkedRegistry;

        public ActiveReactionRegistry(ReactionRegistry<T> linkedRegistry) {
            registry = new IntRBTreeSet();
            searchList = new Int2ObjectRBTreeMap<>();
            this.linkedRegistry = linkedRegistry;
        }

        public void resetTo(int[] idList) {
            registry.clear();
            searchList.clear();
            for (int j : idList) {
                activate(j);
            }
        }

        public boolean contains(int reactionId) {
            return registry.contains(reactionId);
        }

        public void activate(int reactionId) {
            registry.add(reactionId);
            var reaction = linkedRegistry.get(reactionId);
            reaction.forEachReagent(value -> {
                if (searchList.containsKey(value)) {
                    searchList.get(value).add(reactionId);
                } else {
                    IntRBTreeSet newList = new IntRBTreeSet();
                    newList.add(reactionId);
                    searchList.put(value, newList);
                }
            });
        }

        public void inactivate(int reactionId) {
            registry.remove(reactionId);
            var reaction = linkedRegistry.get(reactionId);
            reaction.forEachReagent(value -> {
                IntRBTreeSet set = searchList.get(value);
                set.remove(reactionId);
                if (set.isEmpty()) searchList.remove(value);
            });
        }

        public void forEachCandidate(int consId, IntConsumer consumer) {
            if (searchList.containsKey(consId)) {
                for (int c : searchList.get(consId)) {
                    consumer.accept(c);
                }
            }
        }

        public void forEachReaction(IntConsumer consumer) {
            for (int r : registry) {
                consumer.accept(r);
            }
        }

        public void checkForActivation(int consId) {
            linkedRegistry.forEachCandidate(consId, new IntConsumer() {
                boolean flag;
                @Override
                public void accept(int r) {
                    if (!contains(r)) {
                        flag = false;
                        linkedRegistry.get(r).forEachReagent1(value -> {
                            flag |= existingConstituents.get(value) == 0;
                        });
                        if (!flag) {
                            activate(r);
                            return;
                        }
                        flag = false;
                        linkedRegistry.get(r).forEachReagent2(value -> {
                            flag |= existingConstituents.get(value) == 0;
                        });
                        if (!flag) activate(r);
                    }
                }
            });
        }

        public void checkForInactivation(int consId) {
            forEachCandidate(consId, new IntConsumer() {
                boolean flag;
                @Override
                public void accept(int r) {
                    flag = false;
                    linkedRegistry.get(r).forEachReagent1(value -> {
                        flag |= existingConstituents.get(value) == 0;
                    });
                    if (!flag) return;
                    flag = false;
                    linkedRegistry.get(r).forEachReagent2(value -> {
                        flag |= existingConstituents.get(value) == 0;
                    });
                    if (flag) inactivate(r);
                }
            });
        }

    }


    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        activeInnerReactions.resetTo(tag.getIntArray("active_inner_reactions"));
        activeSurfaceReactions.resetTo(tag.getIntArray("active_surface_reactions"));
        existingConstituents = new Int2IntLinkedOpenHashMap(tag.getIntArray("existing_constituents"),
                tag.getIntArray("existing_constituent_frequency"));
        for (int i = 0; i < sectionsData.length; ++i) {
            sectionsData[i] = new PaletteContainer<>(tag.getCompound(String.format("%d", i)), SimpleConverter.INSTANCE);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        IntArrayList air = new IntArrayList(), asr = new IntArrayList();
        activeInnerReactions.forEachReaction(air::add);
        activeSurfaceReactions.forEachReaction(asr::add);
        tag.putIntArray("active_inner_reactions", air.toIntArray());
        tag.putIntArray("active_surface_reactions", asr.toIntArray());
        tag.putIntArray("existing_constituents", existingConstituents.keySet().toIntArray());
        tag.putIntArray("existing_constituent_frequency", existingConstituents.values().toIntArray());
        for (int i = 0; i < sectionsData.length; ++i) {
            sectionsData[i].shrink();   // try to shrink when saving
            NbtCompound nbtCompound = new NbtCompound();
            sectionsData[i].writeToNbt(nbtCompound);
            tag.put(String.format("%d", i), nbtCompound);
        }
    }

    private static class SimpleConverter implements DynamicPalette.ValueConverter<FluidLayerSet> {
        public static final SimpleConverter INSTANCE = new SimpleConverter();
        private SimpleConverter() {}
        @Override
        public FluidLayerSet getValue(NbtElement nbtElement) {
            return new FluidLayerSet((NbtCompound) nbtElement);
        }
        @Override
        public NbtElement getNbt(FluidLayerSet value) {
            NbtCompound nbtCompound = new NbtCompound();
            value.writeToNbt(nbtCompound);
            return nbtCompound;
        }
    }

}
