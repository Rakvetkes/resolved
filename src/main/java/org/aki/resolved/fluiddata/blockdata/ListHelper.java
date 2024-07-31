package org.aki.resolved.fluiddata.blockdata;

import java.util.List;
import java.util.ListIterator;

public class ListHelper {

    public interface FloatComparator {
        boolean compare(float a, float b);
    }

    public static class NullListIterator<E> implements ListIterator<E> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public E previous() {
            return null;
        }

        @Override
        public int nextIndex() {
            return -1;
        }

        @Override
        public int previousIndex() {
            return -1;
        }

        @Override
        public void remove() {
            throw new IllegalStateException();
        }

        @Override
        public void set(E e) {
            throw new IllegalStateException();
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }
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
