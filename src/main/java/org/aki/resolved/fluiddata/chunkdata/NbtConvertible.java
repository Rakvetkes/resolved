package org.aki.resolved.fluiddata.chunkdata;

import net.minecraft.nbt.NbtCompound;

public interface NbtConvertible {

    void readFromNbt(NbtCompound nbtCompound);

    void writeToNbt(NbtCompound nbtCompound);

}
