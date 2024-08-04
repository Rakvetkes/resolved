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
    public static final FluidLayerSet NULL_LAYER_SET = new FluidLayerSet(Registered.CONSTITUENT_AIR);
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


    public void forEachLayerMutable(Consumer<FluidLayer> consumer) {
        if (isImmutable()) throw new UnsupportedOperationException();
        for (FluidLayer layer : layers) {
            consumer.accept(layer);
        }
    }

    public void replace(float from, FluidLayer layer) {
        if (isImmutable()) throw new UnsupportedOperationException();
        if (from + layer.getVolume() > FULL_VOLUME) {
            throw new IllegalArgumentException();           // note that this guarantees from < FULL_VOLUME
        }
        ListIterator<FluidLayer> it = layers.listIterator();
        FluidLayer layer1 = it.next();
        float level = 0.0f;
        while (level + layer1.getVolume() <= from) {
            level += layer1.getVolume();
            layer1 = it.next();                             // note this should never reach the end of the list
        }
        if (from == level) {
            it.remove();
        } else {
            it.set(layer1.sliceByVolume(from - level));
        }
        it.add(layer.getMutable());                         // note what's stored here may be exactly the object referred to by layer
        while (level + layer1.getVolume() < from + layer.getVolume()) {
            level += layer1.getVolume();
            layer1 = it.next();                             // note this should never reach the end of the list
            it.remove();
        }
        if (from + layer.getVolume() < level + layer1.getVolume()) {
            it.add(layer1.sliceByVolume(level + layer1.getVolume() - (from + layer.getVolume())));
        }
    }

    public void sort() {
        if (isImmutable()) throw new UnsupportedOperationException();
        int cycleCount = layers.size();
        while (cycleCount-- > 0) {
            int bubblingTimes = cycleCount;
            ListIterator<FluidLayer> it = layers.listIterator();
            FluidLayer layer1 = it.next();
            while (bubblingTimes-- > 0) {
                FluidLayer layer2 = it.next();
                if (layer1.getDensity() > layer2.getDensity()) {
                    it.previous();
                    it.previous();
                    it.set(layer2);
                    if (it.hasPrevious()) {
                        FluidLayer layer3 = it.previous();
                        if (layer3.isCompatible(layer2)) {
                            it.remove();
                            layer2.absorb(layer3);
                        }
                    }
                    it.next();
                    it.next();
                    it.set(layer1);
                    if (it.hasNext()) {
                        FluidLayer layer4 = it.next();
                        if (layer4.isCompatible(layer1)) {
                            it.remove();
                            layer1.absorb(layer4);
                        }
                    }
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
                while (volume1 + lastLayer.getVolume() < volume2) {
                    volume1 += lastLayer.getVolume();
                    lastLayer = it.next();
                }
                if (volume1 + lastLayer.getVolume() > volume2) {
                    it.set(lastLayer.sliceByVolume(volume2 - volume1));
                    it.add(lastLayer.sliceByVolume(volume1 + lastLayer.getVolume() - volume2));
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
            it1.set(layer2.sliceByProportion(exchangeRate));
            it1.add(layer1.sliceByProportion(1.0f - exchangeRate));
            it2.set(layer1.sliceByProportion(exchangeRate));
            it2.add(layer2.sliceByProportion(1.0f - exchangeRate));
        }
        layerSet1.sort();
        layerSet2.sort();
    }

    protected void exchangeVertical(FluidLayerSet layerSet) {
        if (isImmutable() || layerSet.isImmutable()) throw new UnsupportedOperationException();
        boolean flag;
        while ((flag = getTopLayer().isCompatible(layerSet.getBottomLayer()))
            || getTopLayer().getDensity() < layerSet.getBottomLayer().getDensity()) {
            if (flag) {
                FluidLayer layer = getTopLayer();
                float volume = layer.getVolume();
                layer.absorb(layerSet.getBottomLayer());
                replace(FULL_VOLUME - volume, layer.sliceByVolume(volume));
                layerSet.replace(0.0f, layer.sliceByVolume(layerSet.getBottomLayer().getVolume()));
            } else {
                FluidLayer layer1 = getTopLayer(), layer2 = layerSet.getBottomLayer();
                float volume = Math.min(layer1.getVolume(), layer2.getVolume());
                replace(FULL_VOLUME - volume, layer2.sliceByVolume(volume));
                layerSet.replace(0.0f, layer1.sliceByVolume(volume));
            }
            this.sort();                    // this might be changed in the future due to high costs
            layerSet.sort();
        }
    }

    public void exchangeVertical(FluidLayerSet layerSet, boolean isUp) {
        if (isUp) this.exchangeVertical(layerSet);
        else layerSet.exchangeVertical(this);
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
