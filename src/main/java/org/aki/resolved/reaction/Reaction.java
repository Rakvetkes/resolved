package org.aki.resolved.reaction;

import it.unimi.dsi.fastutil.ints.IntConsumer;

public interface Reaction {

    void forEachReagent(IntConsumer consumer);

}
