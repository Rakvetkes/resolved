package org.aki.resolved.manager;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.nbt.NbtCompound;
import org.aki.resolved.util.dpc.NbtConvertible;

public class FluidBlockData implements NbtConvertible {

    private IntArrayList fluidId;
    private IntArrayList fluidVol;

    public int getColor() {
        return 0xDDEEFF;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        fluidId = new IntArrayList(nbtCompound.getIntArray("id"));
        fluidVol = new IntArrayList(nbtCompound.getIntArray("volume"));
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putIntArray("id", fluidId.toIntArray());
        nbtCompound.putIntArray("volume", fluidVol.toIntArray());
    }

}
