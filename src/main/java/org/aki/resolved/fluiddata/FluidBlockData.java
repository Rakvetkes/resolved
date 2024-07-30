package org.aki.resolved.fluiddata;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.aki.resolved.fluiddata.blockdata.FluidLayer;
import org.aki.resolved.fluiddata.blockdata.FluidLayerSet;
import org.aki.resolved.fluiddata.blockdata.reaction.ConstituentRegistry;
import org.aki.resolved.fluiddata.chunkdata.DynamicPalette;
import org.aki.resolved.fluiddata.chunkdata.NbtConvertible;

public class FluidBlockData implements NbtConvertible {

    private FluidLayerSet data;

    public FluidBlockData() {
        data = new FluidLayerSet();
    }

    public FluidBlockData(Fluid fluid) {
        data = new FluidLayerSet();

        int consId = ConstituentRegistry.REGISTRY.get(fluid);
        data.addFromTop(new FluidLayer(new FluidLayer.Constituent(consId,
                1000.0f / ConstituentRegistry.REGISTRY.getAttributes(consId).volume())));
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
        data = new FluidLayerSet();
        data.readFromArray(nbtCompound.getIntArray("id"), nbtCompound.getIntArray("volume"));
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        int length = data.getListLength();
        int[] consId = new int[length];
        int[] volume = new int[length];
        data.writeToArray(consId, volume);
        nbtCompound.putIntArray("id", consId);
        nbtCompound.putIntArray("volume", volume);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
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
