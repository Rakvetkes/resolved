package org.aki.resolved.manager;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class FluidChunkData implements Component, AutoSyncedComponent {

    public static final String ID = "fluid_chunk_data";
    NbtCompound data;

    public FluidChunkData() {
        data = new NbtCompound();
    }

    public @NotNull FluidBlockContent get(int i, int j, int k) {
        // todo
        return new FluidBlockContent();
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        data = tag.getCompound(ID);
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.put(ID, data);
    }
}
