package org.aki.resolved.util.dpc.allocator;

public interface IdAllocator {

    void put(int i, int count);

    default void put(int i) {
        put(i, 1);
    }

    void remove(int i);

    void removeAll(int i);

    @O1
    int count(int i);

    int newId();

    @O1
    int maxValue();

    @O1
    int valueCount();

    IdAllocator copy();

}
