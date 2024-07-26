package org.aki.resolved.manager;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.chunk.Chunk;
import org.aki.resolved.util.dpc.PaletteContainer;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class FluidChunkData implements Component, AutoSyncedComponent {

    public static final String ID = "fluid_chunk_data";
    private static final int EDGE_BITS = 4;
    private static final int SECTION_SIZE = 1 << EDGE_BITS * 3;
    private final PaletteContainer<FluidBlockContent>[] sectionsData;

    public FluidChunkData(Chunk chunk) {
        sectionsData = new PaletteContainer[chunk.countVerticalSections()];
        for (int i = 0; i < sectionsData.length; ++i) {
            sectionsData[i] = new PaletteContainer<>(SECTION_SIZE, null/*todo*/);
        }
    }

    public @NotNull FluidBlockContent get(int i, int j, int k) {
        return sectionsData[j >> 4].get(computeIndex(i, j & ((1 << EDGE_BITS) - 1), k));
    }

    private int computeIndex(int i, int j, int k) {
        return (i << EDGE_BITS * 2) | (j << EDGE_BITS) | k;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound fluidData = tag.getCompound(ID);
        for (int i = 0; i < sectionsData.length; ++i) {
            sectionsData[i] = new PaletteContainer<>(fluidData.getCompound(String.format("%d", i)), null/*todo*/);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound fluidData = new NbtCompound();
        for (int i = 0; i < sectionsData.length; ++i) {
            sectionsData[i].shrink();   // try to shrink when saving
            NbtCompound nbtCompound = new NbtCompound();
            sectionsData[i].writeToNbt(nbtCompound);
            fluidData.put(String.format("%d", i), nbtCompound);
        }
        tag.put(ID, fluidData);
    }
}
