package org.aki.resolved.datarelated.blockdata;

import it.unimi.dsi.fastutil.ints.IntIterator;

import java.util.Iterator;

public class ConstituentTypeIterator implements IntIterator {
    Iterator<Constituent> iterator;
    public ConstituentTypeIterator(Iterator<Constituent> iterator) {
        this.iterator = iterator;
    }
    @Override
    public int nextInt() {
        return iterator.next().consId();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
