package org.aki.resolved.datarelated.reaction;

import org.aki.resolved.datarelated.blockdata.FluidLayer;

import java.util.Iterator;

public interface SurfaceReaction extends ReagentCollection {
    void react(FluidLayer layer1, FluidLayer layer2);
}
