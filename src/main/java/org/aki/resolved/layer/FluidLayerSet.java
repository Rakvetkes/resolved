package org.aki.resolved.layer;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import org.aki.resolved.Registered;
import org.aki.resolved.chunk.NbtConvertible;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.*;

public class FluidLayerSet implements Iterable<Constituent>, NbtConvertible, ListHelper.Copyable<FluidLayerSet> {

    private final LinkedList<FluidLayer> layers;
    private float totalVolume;

    private FluidLayerSet(FluidLayerSet layerSet) {
        layers = ListHelper.copy(layerSet.layers);
        totalVolume = layerSet.totalVolume;
    }

    public FluidLayerSet() {
        layers = new LinkedList<>();
        totalVolume = 0.0f;
    }

    public FluidLayerSet(FluidLayer layer) {
        layers = new LinkedList<>();
        totalVolume = 0.0f;
        this.addFromTop(layer);
    }

    public FluidLayerSet(NbtCompound nbtCompound) {
        layers = new LinkedList<>();
        totalVolume = 0.0f;
        readFromNbt(nbtCompound);
    }

    public float getVolume() {
        return totalVolume;
    }

    private void add(FluidLayer layer, ListIterator<FluidLayer> it, ListHelper.FloatComparator comparator) {
        if (!layer.isAir()) {
            totalVolume += layer.getVolume();
        }
        while (it.hasNext()) {
            FluidLayer currentLayer = it.next();
            if (currentLayer.isCompatible(layer)) {
                currentLayer.combine(layer);
                return;
            } else if (comparator.compare(currentLayer.getDensity(), layer.getDensity())) {
                it.previous();
                it.add(layer);
                return;
            }
        }
        it.add(layer);
    }

    public void addFromTop(FluidLayer layer) {
        add(layer, layers.listIterator(), (a, b) -> a > b);
    }

    public void addFromBottom(FluidLayer layer) {
        add(layer, new ListHelper.ReversedListIterator<>(layers), (a, b) -> a <= b);
    }

    public void fold() {
        ListIterator<FluidLayer> it = new ListHelper.ReversedListIterator<>(layers);    // note this goes from bottom to top
        if (!it.hasNext()) {
            return;
        }
        FluidLayer oldLayer = it.next();
        while (it.hasNext()) {
            FluidLayer currentLayer = it.next();
            if (oldLayer.isCompatible(currentLayer)) {
                oldLayer.combine(currentLayer);
                it.remove();
            } else {
                oldLayer = currentLayer;
            }
        }
        while (it.hasPrevious()) {
            FluidLayer layer = it.previous();
            if (layer.isAir()) {
                it.remove();
            } else {
                break;
            }
        }
    }

    private static void align(FluidLayerSet layerSet1, FluidLayerSet layerSet2) {
        ListIterator<FluidLayer> it1 = new ListHelper.ReversedListIterator<>(layerSet1.layers);
        ListIterator<FluidLayer> it2 = new ListHelper.ReversedListIterator<>(layerSet2.layers);
        ListIterator<FluidLayer> tempIt;
        if (it1.hasNext() && it2.hasNext()) {
            FluidLayer layer1 = it1.next(), layer2 = it2.next(), temp;
            while (true) {
                if (layer1.getVolume() > layer2.getVolume()) {
                    temp = layer1;
                    layer1 = layer2;
                    layer2 = temp;
                    tempIt = it1;
                    it1 = it2;
                    it2 = tempIt;
                }
                if (MathHelper.approximatelyEquals(layer1.getVolume(), layer2.getVolume())) {
                    if (!it1.hasNext() || !it2.hasNext()) {
                        break;
                    }
                    layer1 = it1.next();
                    layer2 = it2.next();
                } else if (layer1.getVolume() < layer2.getVolume()) {
                    it2.set(layer2.sliceByVolume(layer1.getVolume()));
                    it2.add(layer2);
                    if (!it1.hasNext()) {
                        break;
                    } else {
                        layer1 = it1.next();
                    }
                }
            }
        }
        if (it1.hasNext()) {
            tempIt = it1;
            it1 = it2;
            it2 = tempIt;
        }
        while (it2.hasNext()) {
            it1.add(new FluidLayer(new Constituent(Registered.CONSTITUENT_AIR, it2.next().getVolume())));
            // note here the volume per unit of air is assumed to be 1
        }
    }

    public static void exchange(FluidLayerSet layerSet1, FluidLayerSet layerSet2, float exchangeRate) {
        align(layerSet1, layerSet2);
        ListIterator<FluidLayer> it1 = new ListHelper.ReversedListIterator<>(layerSet1.layers);
        ListIterator<FluidLayer> it2 = new ListHelper.ReversedListIterator<>(layerSet2.layers);
        Stack<FluidLayer> stack1 = new Stack<>();
        Stack<FluidLayer> stack2 = new Stack<>();
        while (it1.hasNext()/* && it2.hasNext()*/) {
            FluidLayer layer1 = it1.next();
            FluidLayer layer2 = it2.next();
            stack1.push(layer2.sliceByVolume(exchangeRate));
            stack2.push(layer1.sliceByVolume(exchangeRate));
            layerSet1.totalVolume -= layer1.getVolume() * exchangeRate;
            layerSet2.totalVolume -= layer2.getVolume() * exchangeRate;
        }
        while (!stack1.empty()) {
            layerSet1.addFromBottom(stack1.pop());
            layerSet2.addFromBottom(stack2.pop());
        }
        layerSet1.fold();
        layerSet2.fold();
    }

    public static void exchangeVertical(FluidLayerSet above, FluidLayerSet beneath) {
        ListIterator<FluidLayer> it1 = new ListHelper.ReversedListIterator<>(above.layers);
        final float blockVolume = 1000.0f;
        while (it1.hasNext() && beneath.getVolume() < blockVolume) {
            FluidLayer layer = it1.next();
            if (beneath.getVolume() + layer.getVolume() > blockVolume) {
                beneath.addFromTop(layer.sliceByVolume(blockVolume - beneath.getVolume()));
                above.totalVolume -= blockVolume - beneath.getVolume();
            } else {
                beneath.addFromTop(layer);
                above.totalVolume -= layer.getVolume();
                it1.remove();
            }
        }
    }

    @Override
    public int hashCode() {
        return layers.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof FluidLayerSet && layers.equals(((FluidLayerSet) o).layers));
    }

    @NotNull @Override
    public Iterator<Constituent> iterator() {
        return this.listIterator();
    }

    public ListIterator<Constituent> listIterator() {
        return this.listIterator(0);
    }

    public ListIterator<Constituent> listIterator(int index) {
        return new FluidLayerSetIterator(this, index);
    }

    public FluidLayerSet copy() {
        return new FluidLayerSet(this);
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
        int[] consId = nbtCompound.getIntArray("constituents_id");
        int[] amount = nbtCompound.getIntArray("amount");
        for (int i = 0; i < consId.length; ++i) {
            this.addFromTop(new FluidLayer(new Constituent(consId[i], CastHelper.intToFloat(amount[i]))));
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        int size = this.getSize(), i = 0;
        int[] consId = new int[size], amount = new int[size];
        for (Constituent constituent : this) {
            consId[i] = constituent.consId();
            amount[i] = CastHelper.floatToInt(constituent.amount());
            ++i;
        }
        nbtCompound.putIntArray("constituents_id", consId);
        nbtCompound.putIntArray("amount", amount);
    }

    public static class CastHelper {
        static final ByteBuffer byte4 = ByteBuffer.allocate(4);
        public static int floatToInt(float f) {
            byte4.putFloat(0, f);
            return byte4.getInt(0);
        }
        public static float intToFloat(int f) {
            byte4.putInt(0, f);
            return byte4.getFloat(0);
        }
    }

    public static class FluidLayerSetIterator implements ListIterator<Constituent> {

        private final ListIterator<FluidLayer> it1;
        private ListIterator<Constituent> it2;
        private int index;

        public FluidLayerSetIterator(FluidLayerSet layerSet) {
            this(layerSet, 0);
        }

        public FluidLayerSetIterator(FluidLayerSet layerSet, int index) {
            it1 = layerSet.layers.listIterator();
            it2 = it1.hasNext() ? it1.next().listIterator() : null;
            this.index = index;
            while (index > 0) {
                this.next();
                --index;
            }
        }

        @Override
        public boolean hasNext() {
            return it1.hasNext() || (it2 != null && it2.hasNext());
        }

        @Override
        public Constituent next() {
            if (it2 == null) {
                throw new NoSuchElementException();
            }
            while (!it2.hasNext() && it1.hasNext()) {
                it2 = it1.next().listIterator();
            }
            if (it2.hasNext()) {
                ++index;
                return it2.next();
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public boolean hasPrevious() {
            return it1.hasPrevious() || (it2 != null && it2.hasPrevious());
        }

        @Override
        public Constituent previous() {
            if (it2 == null) {
                throw new NoSuchElementException();
            }
            while (!it2.hasPrevious() && it1.hasPrevious()) {
                FluidLayer layer = it1.previous();
                it2 = layer.listIterator(layer.getSize());
            }
            if (it2.hasPrevious()) {
                --index;
                return it2.previous();
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            if (it2 == null) {
                throw new IllegalStateException();
            } else {
                it2.remove();
            }
        }

        @Override
        public void set(Constituent constituent) {
            if (it2 == null) {
                throw new IllegalStateException();
            } else {
                it2.set(constituent);
            }
        }

        @Override
        public void add(Constituent constituent) {
            if (it2 == null) {
                FluidLayer layer = new FluidLayer(constituent);
                it1.add(layer);
                it2 = layer.listIterator(1);
            } else {
                it2.add(constituent);
            }
        }

    }

}
