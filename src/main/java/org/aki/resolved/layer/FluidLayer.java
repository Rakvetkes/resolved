package org.aki.resolved.layer;

import it.unimi.dsi.fastutil.ints.Int2FloatRBTreeMap;
import org.aki.resolved.Registered;

import java.util.function.BiConsumer;

public class FluidLayer {

    protected final Int2FloatRBTreeMap constituents;        // defRetValue is supposed to be 0
    protected float volume;
    protected float density;

    protected FluidLayer(Int2FloatRBTreeMap constituents, float volume, float density) {
        this.constituents = constituents;
        this.volume = volume;
        this.density = density;
    }

    public FluidLayer(int consId, float amount) {
        constituents = new Int2FloatRBTreeMap();
        constituents.put(consId, amount);
        var attributes = ConstituentRegistry.REGISTRY.getAttributes(consId);
        volume = attributes.volume();
        density = attributes.density();
    }

    public FluidLayer(FluidLayer layer) {
        constituents = new Int2FloatRBTreeMap(layer.constituents);
        volume = layer.volume;
        density = layer.density;
    }

    public int getSize() {
        return constituents.size();
    }

    public float getVolume() {
        return volume;
    }

    public float getDensity() {
        return density;
    }

    public boolean isCompatible(int consId) {
        return CompatibilityRegistry.REGISTRY.checkCompatibility(constituents.firstIntKey(), consId);
    }

    public boolean isCompatible(FluidLayer layer) {
        return layer.isCompatible(constituents.firstIntKey());
    }

    public boolean isAir() {
        return constituents.firstIntKey() == Registered.CONSTITUENT_AIR;
    }

    public float amount(int consId) {
        return constituents.get(consId);
    }

    public FluidLayer sliceByVolume(float volume) {
        return sliceByProportion(volume / this.volume);
    }

    public FluidLayer sliceByProportion(float proportion) {
        FluidLayer sliced = new FluidLayer(new Int2FloatRBTreeMap(), 0.0f, 0.0f);
        for (var entry : constituents.int2FloatEntrySet()) {
            sliced.absorb(entry.getIntKey(), entry.getFloatValue() * proportion);
        }
        return sliced;
    }

    public void forEachConstituent(BiConsumer<Integer, Float> consumer) {
        for (var entry : constituents.int2FloatEntrySet()) {
            consumer.accept(entry.getIntKey(), entry.getFloatValue());
        }
    }


    public void absorb(int consId, float amount) {
        if (isImmutable()) throw new UnsupportedOperationException();
        constituents.put(consId, constituents.get(consId) + amount);
        var attributes = ConstituentRegistry.REGISTRY.getAttributes(consId);
        density = (density * volume + attributes.density() * attributes.volume() * amount) / (volume + attributes.volume() * amount);
        volume = volume + attributes.volume() * amount;
    }

    public void absorb(FluidLayer layer) {
        if (isImmutable()) throw new UnsupportedOperationException();
        for (var entry : layer.constituents.int2FloatEntrySet()) {
            constituents.put(entry.getIntKey(), constituents.get(entry.getIntKey()) + entry.getFloatValue());
        }
        density = (density * volume + layer.density * layer.volume) / (volume + layer.volume);
        volume = volume + layer.volume;
    }


    @Override
    public int hashCode() {
        return constituents.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof FluidLayer && constituents.equals(((FluidLayer) o).constituents));
    }


    public boolean isImmutable() {
        return false;
    }

    public FluidLayer getMutable() {
        return this;
    }

    public FluidLayer getImmutable() {
        return new ImmutableFluidLayer(this);
    }

    private static class ImmutableFluidLayer extends FluidLayer {

        public ImmutableFluidLayer(FluidLayer layer) {
            super(layer.constituents, layer.volume, layer.density);
        }

        public boolean isImmutable() {
            return true;
        }

        public FluidLayer getMutable() {
            return new ImmutableFluidLayer(this);
        }

        public FluidLayer getImmutable() {
            return this;
        }

    }

}
