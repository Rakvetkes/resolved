package org.aki.resolved.manager;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.aki.resolved.util.dpc.DynamicPalette;
import org.aki.resolved.util.dpc.NbtConvertible;

public class FluidBlockData implements NbtConvertible {

    private IntArrayList fluidId;
    private IntArrayList fluidVol;

    public FluidBlockData() {
        fluidId = new IntArrayList();
        fluidVol = new IntArrayList();
    }

    public FluidBlockData(NbtCompound nbtCompound) {
        readFromNbt(nbtCompound);
    }

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

    public static class SimpleConverter implements DynamicPalette.ValueConverter<FluidBlockData> {

        public static final SimpleConverter INSTANCE = new SimpleConverter();

        private SimpleConverter() {

        }

        @Override
        public FluidBlockData getValue(NbtElement nbtElement) {
            return new FluidBlockData((NbtCompound) nbtElement);
        }

        @Override
        public NbtElement getNbt(FluidBlockData value) {
            NbtCompound nbtCompound = new NbtCompound();
            value.writeToNbt(nbtCompound);
            return nbtCompound;
        }

    }

}
