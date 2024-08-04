package org.aki.resolved.reaction;

import org.aki.resolved.layer.FluidLayer;

public interface InnerReaction extends Reaction {

    void react(FluidLayer layer, float temp);

}
