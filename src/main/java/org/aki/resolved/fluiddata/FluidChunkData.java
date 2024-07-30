package org.aki.resolved.fluiddata;

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
    private final int bottomY;

    public static final List<FluidChunkData> SYNC_QUEUE = new LinkedList<>();
    // todo save the chunks need to be in sync with client.

    public FluidChunkData(Chunk chunk) {
        sectionsData = new PaletteContainer[chunk.countVerticalSections()];
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

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        for (int i = 0; i < sectionsData.length; ++i) {
            sectionsData[i] = new PaletteContainer<>(tag.getCompound(String.format("%d", i)), FluidBlockData.SimpleConverter.INSTANCE);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        for (int i = 0; i < sectionsData.length; ++i) {
            sectionsData[i].shrink();   // try to shrink when saving
            NbtCompound nbtCompound = new NbtCompound();
            sectionsData[i].writeToNbt(nbtCompound);
            tag.put(String.format("%d", i), nbtCompound);
        }
    }
}
