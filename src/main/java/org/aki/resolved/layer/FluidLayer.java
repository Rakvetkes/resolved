package org.aki.resolved.layer;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatRBTreeMap;
import org.aki.resolved.Registered;

import java.util.function.BiConsumer;

public class FluidLayer {

    protected final Int2FloatRBTreeMap constituents;        // defRetValue is supposed to be 0
    protected float volume;
    protected float density;

    protected FluidLayer() {
        constituents = new Int2FloatRBTreeMap();
        volume = density = 0.0f;
    }

    protected FluidLayer(Int2FloatRBTreeMap constituents, float volume, float density) {
        this.constituents = constituents;
        this.volume = volume;
        this.density = density;
    }

    public FluidLayer(int consId, float amount) {
        constituents = new Int2FloatRBTreeMap();
        constituents.put(consId, amount);
        var attributes = ConstituentRegistry.REGISTRY.getAttributes(consId);
        volume = attributes.volume() * amount;
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
        return CompClassRegistry.REGISTRY.checkCompatibility(constituents.firstIntKey(), consId);
    }

    public boolean isCompatible(FluidLayer layer) {
        return layer.isCompatible(constituents.firstIntKey());
    }

    public boolean isAir() {
        return constituents.firstIntKey() == Registered.CONSTITUENT_AIR;
    }

    public boolean isSolid() {
        return constituents.firstIntKey() == Registered.CONSTITUENT_SOLID;
    }

    public float amount(int consId) {
        return constituents.get(consId);
    }

    public FluidLayer sliceByVolume(float volume) {
        return sliceByProportion(volume / this.volume);
    }

    public FluidLayer sliceByProportion(float proportion) {
        FluidLayer sliced = new FluidLayer();
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
        constituents.addTo(consId, amount);
        if (constituents.get(consId) == 0.0f) constituents.remove(consId);
        var attributes = ConstituentRegistry.REGISTRY.getAttributes(consId);
        density = (density * volume + attributes.density() * attributes.volume() * amount)
                / (volume + attributes.volume() * amount);
        volume = volume + attributes.volume() * amount;
    }

    public void absorb(FluidLayer layer) {
        if (isImmutable() || !isCompatible(layer)) throw new UnsupportedOperationException();
        for (var entry : layer.constituents.int2FloatEntrySet()) {
            constituents.addTo(entry.getIntKey(), entry.getFloatValue());
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
        if (this == o) return true;
        if (!(o instanceof FluidLayer) || ((FluidLayer) o).getSize() != getSize()) return false;
        var it1 = constituents.int2FloatEntrySet().iterator();
        var it2 = ((FluidLayer) o).constituents.int2FloatEntrySet().iterator();
        while (it1.hasNext()) {
            Int2FloatMap.Entry e1 = it1.next(), e2 = it2.next();
            if (e1.getIntKey() != e2.getIntKey() || FloatComparator.compare(e1.getFloatValue(), e2.getFloatValue()) != 0) {
                return false;
            }
        }
        return true;
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
            return new FluidLayer(this);
        }

        public FluidLayer getImmutable() {
            return this;
        }

    }

}
