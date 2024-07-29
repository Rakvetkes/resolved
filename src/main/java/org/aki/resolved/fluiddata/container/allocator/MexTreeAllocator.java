package org.aki.resolved.fluiddata.container.allocator;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.NoSuchElementException;

public class MexTreeAllocator implements IdAllocator {

    private final IntArrayList array;
    private final IntArrayList sum;
    private int maxValue;
    private int topNode;

    private MexTreeAllocator(IntArrayList array, IntArrayList sum, int maxValue, int topNode) {
        this.array = array;
        this.sum = sum;
        this.maxValue = maxValue;
        this.topNode = topNode;
    }

    public MexTreeAllocator() {
        this.array = new IntArrayList();
        this.sum = new IntArrayList();
        this.maxValue = -1;
        this.topNode = 1;
    }

    private void resetMaxValue() {
        while (maxValue >= 0 && ArrayHelper.get(array, maxValue) == 0) {
            --maxValue;
        }
    }

    private void addTree(int i, int value) {
        i = i + 1;      // indices start from 1
        while (topNode < i) {
            topNode <<= 1;
            ArrayHelper.copy(sum, topNode >> 1, topNode);
        }
        while (i <= topNode) {
            ArrayHelper.add(sum, i - 1, value);
            i += (i & (-i));
        }
    }

    private void shrink() {
        while (topNode > 1 && ArrayHelper.get(sum, topNode - 1) == ArrayHelper.get(sum, (topNode >> 1) - 1)) {
            topNode >>= 1;
        }
    }

    @Override
    public void put(int i, int count) {
        if (ArrayHelper.get(array, i) == 0) {
            addTree(i, 1);
        }
        ArrayHelper.add(array, i, count);
    }

    @Override
    public void remove(int i) {
        int count = ArrayHelper.get(array, i);
        if (count == 0) {
            throw new NoSuchElementException();
        } else if (count == 1) {
            addTree(i, -1);
            shrink();
        }
        ArrayHelper.add(array, i, -1);
        if (maxValue == i) {
            resetMaxValue();
        }
    }

    @Override
    public void removeAll(int i) {
        if (ArrayHelper.get(array, i) > 0) {
            addTree(i, -1);
            shrink();
        }
        ArrayHelper.set(array, i, 0);
        if (maxValue == i) {
            --maxValue;
            resetMaxValue();
        }
    }

    @Override
    public int count(int i) {
        return ArrayHelper.get(array, i);
    }

    @Override
    public int newId() {
        if (ArrayHelper.get(sum, topNode - 1) == topNode) {
            return topNode;
        }
        int i = topNode >> 1;
        while ((i & 1) == 0) {
            int lb = (i & (-i));
            if (ArrayHelper.get(sum, i - 1) < lb) {
                i ^= lb;
            }
            i |= (lb >> 1);
        }
        return i - 1 + ArrayHelper.get(sum, i - 1);
    }

    @Override
    public int maxValue() {
        return maxValue;
    }

    @Override
    public int valueCount() {
        return ArrayHelper.get(sum, topNode - 1);
    }

    @Override
    public IdAllocator copy() {
        return new MexTreeAllocator(array.clone(), sum.clone(), maxValue, topNode);
    }

}
