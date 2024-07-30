package org.aki.resolved.fluiddata;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.aki.resolved.fluiddata.blockdata.FluidLayerSet;
import org.aki.resolved.fluiddata.chunkdata.DynamicPalette;
import org.aki.resolved.fluiddata.chunkdata.NbtConvertible;

public class FluidBlockData implements NbtConvertible {

    private FluidLayerSet data;

    public FluidBlockData() {

    }

    public FluidBlockData(Fluid fluid) {

    }

    public FluidBlockData(NbtCompound nbtCompound) {

        readFromNbt(nbtCompound);
    }

    public static FluidBlockData getFromFluid(Fluid fluid) {
        return new FluidBlockData(fluid);
    }

    public static FluidBlockData getNullData() {
        return new FluidBlockData();
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {

    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {

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

    public int getColor() {
        return 0xDDEEFF; // todo
    }

}
