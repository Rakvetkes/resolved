package org.aki.resolved.chunk;

import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.chunk.Chunk;
import org.aki.resolved.layer.FluidLayerSet;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.LinkedList;
import java.util.List;

public class FluidChunk implements Component, AutoSyncedComponent {

    private static final int SECTION_SIZE = 1 << 4 * 3;
    private final PaletteContainer<FluidLayerSet>[] sectionsData;
    private IntLinkedOpenHashSet activeReactions;
    private Int2IntLinkedOpenHashMap existingConstituents;
    private final int bottomY;

    public static final List<FluidChunk> SYNC_QUEUE = new LinkedList<>();
    // todo save the chunks need to be in sync with client.

    public FluidChunk(Chunk chunk) {
        sectionsData = new PaletteContainer[chunk.countVerticalSections()];
        activeReactions = new IntLinkedOpenHashSet();
        existingConstituents = new Int2IntLinkedOpenHashMap();
        bottomY = chunk.getBottomY();
        for (int i = 0; i < sectionsData.length; ++i) {
            sectionsData[i] = new PaletteContainer<>(SECTION_SIZE, FluidLayerSet.NULL_LAYER_SET, SimpleConverter.INSTANCE);
        }
    }

    public FluidLayerSet getFluidData(int i, int j, int k) {
        return sectionsData[(j - bottomY) >> 4].get(computeIndex(i, (j - bottomY) & 15, k)).getImmutable();
    }

    public void setFluidData(int i, int j, int k, FluidLayerSet data) {
        // todo update constituent existence marks & available reaction marks
        sectionsData[(j - bottomY) >> 4].set(computeIndex(i, (j - bottomY) & 15, k), data);
    }

    private int computeIndex(int i, int j, int k) {
        return (i << 4 * 2) | (j << 4) | k;
    }

    public final IntSortedSet getActiveReactions() {
        return activeReactions;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        activeReactions = new IntLinkedOpenHashSet(tag.getIntArray("active_reactions"));
        existingConstituents = new Int2IntLinkedOpenHashMap(tag.getIntArray("existing_constituents"),
                tag.getIntArray("existing_constituent_frequency"));
        for (int i = 0; i < sectionsData.length; ++i) {
            sectionsData[i] = new PaletteContainer<>(tag.getCompound(String.format("%d", i)), SimpleConverter.INSTANCE);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putIntArray("active_reactions", activeReactions.toIntArray());
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
