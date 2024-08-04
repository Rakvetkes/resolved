package org.aki.resolved.datarelated.reaction;

import org.aki.resolved.datarelated.blockdata.FluidLayer;

public interface InnerReaction extends Reaction {
    void react(FluidLayer layer);
}
