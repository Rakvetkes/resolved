package org.aki.resolved.layer;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.nbt.NbtCompound;
import org.aki.resolved.Registered;
import org.aki.resolved.chunk.NbtConvertible;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Consumer;

public class FluidLayerSet implements NbtConvertible {

    public static final float FULL_VOLUME = 1000.0f;
    public static final float CUT_DOWN_VOLUME = 1.0f;
    public static final FluidLayerSet NULL_LAYER_SET = new FluidLayerSet(Registered.CONSTITUENT_AIR);
    public static final FluidLayerSet SOLID_LAYER_SET = new FluidLayerSet(Registered.CONSTITUENT_SOLID);
    protected final LinkedList<FluidLayer> layers;

    protected FluidLayerSet(LinkedList<FluidLayer> layers) {
        this.layers = layers;
    }

    public FluidLayerSet(FluidLayerSet layerSet) {
        this(new LinkedList<>());
        for (FluidLayer layer : layerSet.layers) {
            this.layers.add(new FluidLayer(layer));
        }
    }

    public FluidLayerSet(int consId) {
        this(new LinkedList<>());
        this.layers.add(new FluidLayer(consId, FULL_VOLUME / ConstituentRegistry.REGISTRY.getAttributes(consId).volume()));
    }

    public FluidLayerSet(NbtCompound nbtCompound) {
        this(new LinkedList<>());
        readFromNbt(nbtCompound);
    }

    public FluidLayer getTopLayer() {
        return layers.getFirst().getImmutable();
    }

    public FluidLayer getBottomLayer() {
        return layers.getLast().getImmutable();
    }

    public void forEachLayer(Consumer<FluidLayer> consumer) {
        for (FluidLayer layer : layers) {
            consumer.accept(layer.getImmutable());
        }
    }

    public boolean isAir() {
        return layers.size() == 1 && layers.getFirst().isAir();
    }


    public FluidLayer getTopLayerMutable() {
        if (isImmutable()) throw new UnsupportedOperationException();
        return layers.getFirst();
    }

    public FluidLayer getBottomLayerMutable() {
        if (isImmutable()) throw new UnsupportedOperationException();
        return layers.getLast();
    }

    public void forEachLayerMutable(Consumer<FluidLayer> consumer) {
        if (isImmutable()) throw new UnsupportedOperationException();
        for (FluidLayer layer : layers) {
            consumer.accept(layer);
        }
    }

    public void fill(int consId) {                          // note this method doesn't call sort()
        if (isImmutable()) throw new UnsupportedOperationException();
        ListIterator<FluidLayer> it = layers.listIterator();
        while (it.hasNext()) {
            FluidLayer layer = it.next();
            if (layer.isAir()) {
                it.set(new FluidLayer(consId, layer.getVolume() / ConstituentRegistry.REGISTRY.getAttributes(consId).volume()));
            }
        }
    }

    public void replace(float from, FluidLayer layer) {
        if (isImmutable()) throw new UnsupportedOperationException();
        if (FloatComparator.compare(from + layer.getVolume(), FULL_VOLUME) == 1) {
            throw new IllegalArgumentException();           // note that this guarantees from < FULL_VOLUME
        }
        ListIterator<FluidLayer> it = layers.listIterator();
        FluidLayer layer1 = it.next();
        float level = 0.0f;
        while (FloatComparator.compare(level + layer1.getVolume(), from) == -1) {
            level += layer1.getVolume();
            layer1 = it.next();                             // note this should never reach the end of the list
        }
        if (level == from) {
            it.remove();
        } else {
            it.set(layer1.sliceByVolume(from - level));
        }
        it.add(layer.getMutable());                         // note what's stored here may be exactly the object referred to by layer
        while (FloatComparator.compare(level + layer1.getVolume(), from + layer.getVolume()) == -1) {
            level += layer1.getVolume();
            layer1 = it.next();                             // note this should never reach the end of the list
            it.remove();
        }
        if (level + layer1.getVolume() > from + layer.getVolume()) {
            it.add(layer1.sliceByVolume(level + layer1.getVolume() - (from + layer.getVolume())));
        }
    }

    public void sort() {
        if (isImmutable()) throw new UnsupportedOperationException();
        ListIterator<FluidLayer> it = layers.listIterator();
        FluidLayer layer = it.next();
        while (it.hasNext()) {
            FluidLayer layer1 = it.next();
            if (layer.isCompatible(layer1)) {
                it.remove();
                layer.absorb(layer1);
            } else {
                layer = layer1;
            }
        }
        int cycleCount = layers.size();
        while (cycleCount-- > 0) {
            int bubblingTimes = layers.size() - 1;
            ListIterator<FluidLayer> it1 = layers.listIterator();
            FluidLayer layer1 = it1.next();
            while (bubblingTimes-- > 0) {
                FluidLayer layer2 = it1.next();
                if (!layer1.isSolid() && !layer2.isSolid() && layer1.getDensity() > layer2.getDensity()) {
                    it1.previous();
                    it1.previous();
                    it1.set(layer2);
                    if (it1.hasPrevious()) {
                        FluidLayer layer3 = it1.previous();
                        if (layer3.isCompatible(layer2)) {
                            it1.remove();
                            layer2.absorb(layer3);
                            --cycleCount;
                        }
                    }
                    it1.next();
                    it1.next();
                    it1.set(layer1);
                    if (it1.hasNext()) {
                        FluidLayer layer4 = it1.next();
                        if (layer4.isCompatible(layer1)) {
                            it1.remove();
                            layer1.absorb(layer4);
                            --cycleCount;
                            --bubblingTimes;
                        }
                    }
                } else {
                    layer1 = layer2;
                }
            }
        }
    }

    public void align(FluidLayerSet layerSet) {
        if (isImmutable()) throw new UnsupportedOperationException();
        layerSet.forEachLayer(new Consumer<FluidLayer>() {
            final ListIterator<FluidLayer> it = layers.listIterator();
            FluidLayer lastLayer = it.next();
            float volume1 = 0.0f, volume2 = 0.0f;
            @Override
            public void accept(FluidLayer layer) {
                volume2 += layer.getVolume();
                while (FloatComparator.compare(volume1 + lastLayer.getVolume(), volume2) == -1) {
                    volume1 += lastLayer.getVolume();
                    lastLayer = it.next();
                }
                if (FloatComparator.compare(volume1 + lastLayer.getVolume(), volume2) == 1) {
                    it.set(lastLayer.sliceByVolume(volume2 - volume1));
                    it.add(lastLayer.sliceByVolume(volume1 + lastLayer.getVolume() - volume2));
                    it.previous(); it.next();
                }
            }
        });
    }

    public static void exchange(FluidLayerSet layerSet1, FluidLayerSet layerSet2, float exchangeRate) {
        if (layerSet1.isImmutable() || layerSet2.isImmutable()) throw new UnsupportedOperationException();
        layerSet1.align(layerSet2);
        layerSet2.align(layerSet1);
        ListIterator<FluidLayer> it1 = layerSet1.layers.listIterator();
        ListIterator<FluidLayer> it2 = layerSet2.layers.listIterator();
        while (it1.hasNext()/* && it2.hasNext()*/) {
            FluidLayer layer1 = it1.next();
            FluidLayer layer2 = it2.next();
            if (!layer1.isSolid() && !layer2.isSolid() && layer1.getVolume()
                    * Math.min(exchangeRate, 1.0f - exchangeRate) >= CUT_DOWN_VOLUME) {
                it1.set(layer2.sliceByProportion(exchangeRate));
                it1.add(layer1.sliceByProportion(1.0f - exchangeRate));
                it2.set(layer1.sliceByProportion(exchangeRate));
                it2.add(layer2.sliceByProportion(1.0f - exchangeRate));
            }
        }
        layerSet1.sort();
        layerSet2.sort();
    }

    protected static void exchangeVertical(FluidLayerSet layerSet1, FluidLayerSet layerSet2) {
        if (layerSet1.isImmutable() || layerSet2.isImmutable()) throw new UnsupportedOperationException();
        if (!layerSet1.getTopLayer().isSolid() && !layerSet2.getBottomLayer().isSolid()) {
            if (layerSet1.getTopLayer().isCompatible(layerSet2.getBottomLayer())) {
                FluidLayer layer = layerSet1.getTopLayer().getMutable();
                float volume = layer.getVolume();
                float volume2 = layerSet2.getBottomLayer().getVolume();
                layer.absorb(layerSet2.getBottomLayer());
                layerSet1.replace(0.0f, layer.sliceByVolume(volume));
                layerSet2.replace(FULL_VOLUME - volume2, layer.sliceByVolume(volume2));
            } else if (layerSet1.getTopLayer().getDensity() < layerSet2.getBottomLayer().getDensity()) {
                FluidLayer layer1 = layerSet1.getTopLayer();
                FluidLayer layer2 = layerSet2.getBottomLayer();
                float volume = Math.min(layer1.getVolume(), layer2.getVolume());
                layerSet1.replace(0.0f, layer2.sliceByVolume(volume));
                layerSet2.replace(FULL_VOLUME - volume, layer1.sliceByVolume(volume));
            } else {
                return;
            }
            layerSet1.sort();
            layerSet2.sort();
        }

    }

    public void exchangeVertical(FluidLayerSet layerSet, boolean isUp) {
        if (isUp) exchangeVertical(this, layerSet);
        else exchangeVertical(layerSet, this);
    }


    @Override
    public int hashCode() {
        return layers.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof FluidLayerSet && layers.equals(((FluidLayerSet) o).layers));
    }

    public int getSize() {
        int listLength = 0;
        for (FluidLayer layer : layers) {
            listLength += layer.getSize();
        }
        return listLength;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        if (isImmutable()) throw new UnsupportedOperationException();
        int[] consId = nbtCompound.getIntArray("constituents_id");
        int[] amount = nbtCompound.getIntArray("amount");
        layers.clear();
        for (int i = 0; i < consId.length; ++i) {
            float floatAmount = Float.intBitsToFloat(amount[i]);
            if (layers.isEmpty() || !layers.getLast().isCompatible(consId[i])) {
                layers.add(new FluidLayer(consId[i], floatAmount));
            } else {
                layers.getLast().absorb(consId[i], floatAmount);
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        IntArrayList consId = new IntArrayList();
        IntArrayList amount = new IntArrayList();
        for (FluidLayer layer : this.layers) {
            layer.forEachConstituent((integer, aFloat) -> {
                consId.add(integer.intValue());
                amount.add(Float.floatToIntBits(aFloat));
            });
        }
        nbtCompound.putIntArray("constituents_id", consId.toIntArray());
        nbtCompound.putIntArray("amount", amount.toIntArray());
    }


    public boolean isImmutable() {
        return false;
    }

    public FluidLayerSet getMutable() {
        return this;
    }

    public FluidLayerSet getImmutable() {
        return new ImmutableFluidLayerSet(this);
    }

    private static class ImmutableFluidLayerSet extends FluidLayerSet {

        public ImmutableFluidLayerSet(FluidLayerSet layerSet) {
            super(layerSet.layers);
        }

        public boolean isImmutable() {
            return true;
        }

        public FluidLayerSet getMutable() {
            return new FluidLayerSet(this);
        }

        public FluidLayerSet getImmutable() {
            return this;
        }

    }

}
