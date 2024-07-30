package org.aki.resolved.fluiddata.blockdata;

import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Contract;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;

public class FluidLayerSet {

    private final LinkedList<FluidLayer> layers;

    public FluidLayerSet() {
        layers = new LinkedList<>();
    }

    private void add(FluidLayer layer, ListIterator<FluidLayer> it, ListHelper.FloatComparator comparator) {
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
        ListIterator<FluidLayer> it = layers.listIterator();    // note this goes from top to bottom
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
    }

    private static void align(FluidLayerSet layerSet1, FluidLayerSet layerSet2) {
        ListIterator<FluidLayer> it1 = new ListHelper.ReversedListIterator<>(layerSet1.layers);
        ListIterator<FluidLayer> it2 = new ListHelper.ReversedListIterator<>(layerSet2.layers);
        ListIterator<FluidLayer> tempIt;
        if (!it1.hasNext() || !it2.hasNext()) {
            return;
        }
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
                    return;
                }
                layer1 = it1.next();
                layer2 = it2.next();
            } else if (layer1.getVolume() < layer2.getVolume()) {
                it2.set(layer2.sliceByVolume(layer1.getVolume()));
                it2.add(layer2);
                if (!it1.hasNext()) {
                    return;
                } else {
                    layer1 = it1.next();
                }
            }
        }
    }

    public static void exchange(FluidLayerSet layerSet1, FluidLayerSet layerSet2, float exchangeRate) {
        align(layerSet1, layerSet2);
        ListIterator<FluidLayer> it1 = new ListHelper.ReversedListIterator<>(layerSet1.layers);
        ListIterator<FluidLayer> it2 = new ListHelper.ReversedListIterator<>(layerSet2.layers);
        Stack<FluidLayer> stack1 = new Stack<>();
        Stack<FluidLayer> stack2 = new Stack<>();
        while (it1.hasNext() && it2.hasNext()) {
            FluidLayer layer1 = it1.next();
            FluidLayer layer2 = it2.next();
            stack1.push(layer2.sliceByVolume(exchangeRate));
            stack2.push(layer1.sliceByVolume(exchangeRate));
        }
        layerSet1.fold();
        layerSet2.fold();
        while (!stack1.empty()) {
            layerSet1.addFromBottom(stack1.pop());
            layerSet2.addFromBottom(stack2.pop());
        }
    }

    @Override
    public int hashCode() {
        return layers.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FluidLayerSet && hashCode() == o.hashCode();
    }

    public void readFromArray(int[] consId, int[] amount) {
        for (int i = 0; i < consId.length; ++i) {
            this.addFromTop(new FluidLayer(new FluidLayer.Constituent(consId[i], CastHelper.intToFloat(amount[i]))));
        }
    }

    public int getListLength() {
        int listLength = 0;
        for (FluidLayer layer : layers) {
            for (FluidLayer.Constituent constituent : layer) {
                ++listLength;
            }
        }
        return listLength;
    }

    public void writeToArray(int[] consId, int[] amount) {
        int i = 0;
        for (FluidLayer layer : layers) {
            for (FluidLayer.Constituent constituent : layer) {
                consId[i] = constituent.consId();
                amount[i] = CastHelper.floatToInt(constituent.amount());
                ++i;
            }
        }
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

}
