package org.aki.resolved.util.dpc;

import net.minecraft.nbt.NbtCompound;

public interface NbtConvertible {

    void readFromNbt(NbtCompound nbtCompound);

    void writeToNbt(NbtCompound nbtCompound);

}
