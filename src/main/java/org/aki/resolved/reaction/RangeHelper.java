package org.aki.resolved.reaction;

import it.unimi.dsi.fastutil.floats.FloatFloatImmutablePair;
import org.jetbrains.annotations.NotNull;

public final class RangeHelper {
    public static boolean haveCommon(@NotNull FloatFloatImmutablePair a, @NotNull FloatFloatImmutablePair b) {
        return a.rightFloat() > b.leftFloat() && a.leftFloat() < b.rightFloat();
    }
    public static FloatFloatImmutablePair getIntersection(FloatFloatImmutablePair a, FloatFloatImmutablePair b) {
        if (haveCommon(a, b))
            return FloatFloatImmutablePair.of(Math.max(a.leftFloat(), b.leftFloat()), Math.min(a.rightFloat(), b.rightFloat()));
        return FloatFloatImmutablePair.of(0f, 0f);
    }
    public static float getLength(FloatFloatImmutablePair a) {
        return a.rightFloat() - a.leftFloat();
    }
    public static boolean isEmpty(FloatFloatImmutablePair a) {
        return a.leftFloat() == a.rightFloat();
    }
}
