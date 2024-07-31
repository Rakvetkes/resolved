package org.aki.resolved.fluiddata;

import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.chunk.Chunk;
import org.aki.resolved.fluiddata.chunkdata.PaletteContainer;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.LinkedList;
import java.util.List;

public class FluidChunkData implements Component, AutoSyncedComponent {

    private static final int SECTION_SIZE = 1 << 4 * 3;
    private final PaletteContainer<FluidBlockData>[] sectionsData;
    private IntLinkedOpenHashSet activeReactions;
    private Int2IntLinkedOpenHashMap existingConstituents;
    private final int bottomY;

    public static final List<FluidChunkData> SYNC_QUEUE = new LinkedList<>();
    // todo save the chunks need to be in sync with client.

    public FluidChunkData(Chunk chunk) {
        sectionsData = new PaletteContainer[chunk.countVerticalSections()];
        activeReactions = new IntLinkedOpenHashSet();
        existingConstituents = new Int2IntLinkedOpenHashMap();
        bottomY = chunk.getBottomY();
        FluidBlockData nullData = FluidBlockData.getNullData();
        for (int i = 0; i < sectionsData.length; ++i) {
            sectionsData[i] = new PaletteContainer<>(SECTION_SIZE, nullData, FluidBlockData.SimpleConverter.INSTANCE);
        }
    }

    public @NotNull FluidBlockData getFluidData(int i, int j, int k) {
        return sectionsData[(j - bottomY) >> 4].get(computeIndex(i, (j - bottomY) & 15, k));
    }

    public void setFluidData(int i, int j, int k, FluidBlockData data) {
        sectionsData[(j - bottomY) >> 4].set(computeIndex(i, (j - bottomY) & 15, k), data);
    }

    private int computeIndex(int i, int j, int k) {
        return (i << 4 * 2) | (j << 4) | k;
    }

    public final IntSortedSet getActiveReactions() {
        return activeReactions;
    }

    public boolean checkExistence(int consId) {
        return existingConstituents.containsKey(consId);
    }

    public void markActive(int reactionId) {
        activeReactions.add(reactionId);
    }

    public void markInactive(int reactionId) {
        activeReactions.remove(reactionId);
    }

    public void markExistenceInBlock(int consId) {
        if (existingConstituents.containsKey(consId)) {
            existingConstituents.addTo(consId, 1);
        } else {
            existingConstituents.put(consId, 1);
        }
    }

    public void markDisappearanceInBlock(int consId) {
        existingConstituents.addTo(consId, -1);
        if (existingConstituents.get(consId) == 0) {
            existingConstituents.remove(consId);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        activeReactions = new IntLinkedOpenHashSet(tag.getIntArray("active_reactions"));
        existingConstituents = new Int2IntLinkedOpenHashMap(tag.getIntArray("existing_constituents"),
                tag.getIntArray("existing_constituent_frequency"));
        for (int i = 0; i < sectionsData.length; ++i) {
            sectionsData[i] = new PaletteContainer<>(tag.getCompound(String.format("%d", i)), FluidBlockData.SimpleConverter.INSTANCE);
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
}
