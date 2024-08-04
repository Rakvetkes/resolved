package org.aki.resolved.chunk.idallocator;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public class BiArrayAllocator implements IdAllocator {

    protected IntArrayList freed, idCount;
    protected int countSum;

    public BiArrayAllocator() {
        freed = new IntArrayList();
        idCount = new IntArrayList();
        countSum = 0;
    }

    protected BiArrayAllocator(BiArrayAllocator allocator) {
        freed = allocator.freed.clone();
        idCount = allocator.idCount.clone();
        countSum = allocator.countSum;
    }

    @Override
    public void put(int i, int count) {
        if (ArrayHelper.get(idCount, i) == 0) {
            ++countSum;
        }
        ArrayHelper.add(idCount, i, count);
    }

    @Override
    public void remove(int i) {
        if (ArrayHelper.get(idCount, i) == 1) {
            this.removeAll(i);
        } else {
            ArrayHelper.add(idCount, i, -1);
        }
    }

    @Override
    public void removeAll(int i) {
        if (ArrayHelper.get(idCount, i) != 0) {
            --countSum;
            freed.addLast(i);
            ArrayHelper.set(idCount, i, 0);
            while (!idCount.isEmpty() && idCount.getLast() == 0) {
                idCount.removeLast();
            }
        }
    }

    @Override
    public int count(int i) {
        return ArrayHelper.get(idCount, i);
    }

    @Override
    public int newId() {
        while (!freed.isEmpty() && ArrayHelper.get(idCount, freed.getLast()) != 0) {
            freed.removeLast();
        }
        if (freed.isEmpty()) {
            return idCount.size();
        } else {
            return freed.getLast();
        }
    }

    @Override
    public int maxValue() {
        return idCount.size() - 1;
    }

    @Override
    public int valueCount() {
        return countSum;
    }

    @Override
    public IdAllocator copy() {
        return new BiArrayAllocator(this);
    }
}
