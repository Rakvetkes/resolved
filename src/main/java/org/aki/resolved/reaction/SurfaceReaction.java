package org.aki.resolved.reaction;

import org.aki.resolved.layer.FluidLayer;

public interface SurfaceReaction extends Reaction {

    void react(FluidLayer layer1, FluidLayer layer2, float temp);

}
