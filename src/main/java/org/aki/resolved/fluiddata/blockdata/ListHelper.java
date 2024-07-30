package org.aki.resolved.fluiddata.blockdata;

import java.util.List;
import java.util.ListIterator;

public class ListHelper {

    public interface FloatComparator {

        boolean compare(float a, float b);

    }

    public static class ReversedListIterator<E> implements ListIterator<E> {

        private final ListIterator<E> iterator;

        public ReversedListIterator(ListIterator<E> iterator) {
            this.iterator = iterator;
        }

        public ReversedListIterator(List<E> list) {
            this.iterator = list.listIterator(list.size());
        }

        @Override
        public boolean hasNext() {
            return iterator.hasPrevious();
        }

        @Override
        public E next() {
            return iterator.previous();
        }

        @Override
        public boolean hasPrevious() {
            return iterator.hasNext();
        }

        @Override
        public E previous() {
            return iterator.next();
        }

        @Override
        public int nextIndex() {
            return iterator.previousIndex();
        }

        @Override
        public int previousIndex() {
            return iterator.nextIndex();
        }

        @Override
        public void remove() {
            iterator.remove();
        }

        @Override
        public void set(E e) {
            iterator.set(e);
        }

        @Override
        public void add(E e) {
            iterator.add(e);
        }

    }

}
