package org.aki.resolved.fluiddata.chunkdata.allocator;

public interface IdAllocator {

    void put(int i, int count);

    default void put(int i) {
        put(i, 1);
    }

    void remove(int i);

    void removeAll(int i);

    int count(int i);

    int newId();

    int maxValue();

    int valueCount();

    IdAllocator copy();

}
