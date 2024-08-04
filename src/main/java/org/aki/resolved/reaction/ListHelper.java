package org.aki.resolved.reaction;

import java.util.ListIterator;

public class ListHelper {

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

}
