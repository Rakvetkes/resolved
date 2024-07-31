package org.aki.resolved.datarelated.chunkdata;

import net.minecraft.nbt.NbtCompound;

public interface NbtConvertible {

    void readFromNbt(NbtCompound nbtCompound);

    void writeToNbt(NbtCompound nbtCompound);

}
