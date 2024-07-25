package org.aki.resolved.util.dpc.mex;

public interface MexContainer {

    void put(int i);

    void remove(int i);

    void removeAll(int i);

    @O1
    int count(int i);

    int mex();

    @O1
    int maxValue();

    @O1
    int valueCount();

    MexContainer copy();

}
