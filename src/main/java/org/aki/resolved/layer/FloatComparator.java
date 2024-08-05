package org.aki.resolved.layer;

public class FloatComparator {

    private static final float EQUALITY_LIMIT = 0.01f;

    public static int compare(float a, float b) {
//        return a == b ? 0 : (a > b ? 1 : -1);
        return Math.abs(a - b) < EQUALITY_LIMIT ? 0 : (a > b ? 1 : -1);
    }

}
