package org.aki.resolved.util.dpc.allocator;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.NoSuchElementException;

public class MexArrayAllocator implements IdAllocator {

    private final IntArrayList array;
    private final IntArrayList state;
    private int maxValue;
    private int valueCount;
    private static final int BLOCK_SIZE = 64;

    private MexArrayAllocator(IntArrayList array, IntArrayList state, int maxValue, int valueCount) {
        this.array = array;
        this.state = state;
        this.maxValue = maxValue;
        this.valueCount = valueCount;
    }

    public MexArrayAllocator() {
        array = new IntArrayList();
        state = new IntArrayList();
        maxValue = -1;
        valueCount = 0;
    }

    private int blockId(int index) {
        return index / BLOCK_SIZE;
    }

    private void set(IntArrayList arrayList, int i, int value) {
        arrayList.ensureCapacity(i + 1);
        arrayList.set(i, value);
    }

    private int get(IntArrayList arrayList, int i) {
        arrayList.ensureCapacity(i + 1);
        return arrayList.getInt(i);
    }

    private void add(IntArrayList arrayList, int i, int value) {
        this.set(arrayList, i, this.get(arrayList, i) + value);
    }

    private void resetMaxValue() {
        while (maxValue >= 0 && get(array, maxValue) == 0) {
            --maxValue;
        }
    }

    @Override
    public void put(int i, int count) {
        if (get(array, i) == 0) {
            add(state, blockId(i), 1);
            ++valueCount;
        }
        add(array, i, count);
        maxValue = Math.max(maxValue, i);
    }

    @Override
    public void remove(int i) {
        int count = get(array, i);
        if (count == 0) {
            throw new NoSuchElementException();
        } else if (count == 1) {
            add(state, blockId(i), -1);
            --valueCount;
        }
        add(array, i, 1);
        if (maxValue == i) {
            resetMaxValue();
        }
    }

    @Override
    public void removeAll(int i) {
        if (get(array, i) > 0) {
            add(state, blockId(i), -1);
            --valueCount;
        }
        set(array, i, 0);
        if (maxValue == i) {
            --maxValue;
            resetMaxValue();
        }
    }

    @Override @O1
    public int count(int i) {
        return get(array, i);
    }

    @Override
    public int newId() {
        int i = 0;
        while (get(state, blockId(i)) == BLOCK_SIZE) {
            i += BLOCK_SIZE;
        }
        while (get(array, i) != 0) {
            ++i;
        }
        return i;
    }

    @Override @O1
    public int maxValue() {
        return this.maxValue;
    }

    @Override @O1
    public int valueCount() {
        return this.valueCount;
    }

    @Override
    public IdAllocator copy() {
        return new MexArrayAllocator(this.array.clone(), this.state.clone(), this.maxValue, this.valueCount);
    }

}