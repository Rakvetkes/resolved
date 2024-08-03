package org.aki.resolved.datarelated.reaction;

import org.aki.resolved.datarelated.blockdata.FluidLayer;

public interface InnerReaction extends ReagentCollection {
    void react(FluidLayer layer);
}
