package org.aki.resolved.util.dpc.allocator;

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

    private void ensureNonNegative(int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void set(IntArrayList arrayList, int i, int value) {
        ensureNonNegative(i);
        arrayList.ensureCapacity(i + 1);
        arrayList.set(i, value);
    }

    private int get(IntArrayList arrayList, int i) {
        ensureNonNegative(i);
        arrayList.ensureCapacity(i + 1);
        return arrayList.getInt(i);
    }

    private void add(IntArrayList arrayList, int i, int value) {
        this.set(arrayList, i, this.get(arrayList, i) + value);
    }

    private void copy(IntArrayList arrayList, int i, int j) {
        this.set(arrayList, j, this.get(arrayList, i));
    }

    private void resetMaxValue() {
        while (maxValue >= 0 && get(array, maxValue) == 0) {
            --maxValue;
        }
    }

    private void addTree(int i, int value) {
        i = i + 1;      // indices start from 1
        while (topNode < i) {
            topNode <<= 1;
            copy(sum, topNode >> 1, topNode);
        }
        while (i <= topNode) {
            add(sum, i - 1, value);
            i += (i & (-i));
        }
    }

    private void shrink() {
        while (topNode > 1 && get(sum, topNode - 1) == get(sum, (topNode >> 1) - 1)) {
            topNode >>= 1;
        }
    }

    @Override
    public void put(int i, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException();
        }
        if (get(array, i) == 0) {
            addTree(i, 1);
        }
        add(array, i, count);
    }

    @Override
    public void remove(int i) {
        int count = get(array, i);
        if (count == 0) {
            throw new NoSuchElementException();
        } else if (count == 1) {
            addTree(i, -1);
            shrink();
        }
        add(array, i, -1);
        if (maxValue == i) {
            resetMaxValue();
        }
    }

    @Override
    public void removeAll(int i) {
        if (get(array, i) > 0) {
            addTree(i, -1);
            shrink();
        }
        set(array, i, 0);
        if (maxValue == i) {
            --maxValue;
            resetMaxValue();
        }
    }

    @Override
    public int count(int i) {
        return get(array, i);
    }

    @Override
    public int newId() {
        if (get(sum, topNode - 1) == topNode) {
            return topNode;
        }
        int i = topNode >> 1;
        while ((i & 1) == 0) {
            int lb = (i & (-i));
            if (get(sum, i - 1) < lb) {
                i ^= lb;
            }
            i |= (lb >> 1);
        }
        return i - 1 + get(sum, i - 1);
    }

    @Override
    public int maxValue() {
        return maxValue;
    }

    @Override
    public int valueCount() {
        return get(sum, topNode - 1);
    }

    @Override
    public IdAllocator copy() {
        return new MexTreeAllocator(array.clone(), sum.clone(), maxValue, topNode);
    }

}
