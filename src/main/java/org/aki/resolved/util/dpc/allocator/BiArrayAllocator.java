package org.aki.resolved.util.dpc.allocator;

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
        idCount.ensureCapacity(i);
        if (idCount.getInt(i) == 0) {
            ++countSum;
        }
        idCount.add(i, count);
    }

    @Override
    public void remove(int i) {
        if (idCount.getInt(i) == 1) {
            this.removeAll(i);
        } else {
            idCount.add(i, -1);
        }
    }

    @Override
    public void removeAll(int i) {
        if (idCount.getInt(i) != 0) {
            --countSum;
            freed.addLast(i);
            idCount.set(i, 0);
            while (!idCount.isEmpty() && idCount.getLast() == 0) {
                idCount.removeLast();
            }
        }
    }

    @Override
    public int count(int i) {
        return idCount.getInt(i);
    }

    @Override
    public int newId() {
        while (!freed.isEmpty() && idCount.getInt(freed.getLast()) != 0) {
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
        return idCount.size();
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
