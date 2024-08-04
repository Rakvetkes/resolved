package org.aki.resolved.datarelated.reaction;

import org.aki.resolved.datarelated.blockdata.FluidLayer;

public interface SurfaceReaction extends Reaction {
    void react(FluidLayer layer1, FluidLayer layer2);
}
