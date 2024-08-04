package org.aki.resolved.chunk.idallocator;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public class ArrayHelper {

    public static void ensureCapacity(IntArrayList arrayList, int capacity) {
        while (arrayList.size() < capacity) {
            arrayList.add(0);
        }
    }

    public static void set(IntArrayList arrayList, int index, int value) {
        ensureCapacity(arrayList, index + 1);
        arrayList.set(index, value);
    }

    public static int get(IntArrayList arrayList, int index) {
        return index >= arrayList.size() ? 0 : arrayList.getInt(index);
    }

    public static void add(IntArrayList arrayList, int index, int value) {
        set(arrayList, index, get(arrayList, index) + value);
    }

    public static void copy(IntArrayList arrayList, int from, int to) {
        set(arrayList, to, get(arrayList, from));
    }

}
