package org.aki.resolved.reaction;

import it.unimi.dsi.fastutil.ints.IntConsumer;

public interface Reaction {

    void forEachReagent1(IntConsumer consumer);

    void forEachReagent2(IntConsumer consumer);

    default void forEachReagent(IntConsumer consumer) {
        forEachReagent1(consumer);
        forEachReagent2(consumer);
    }

}
