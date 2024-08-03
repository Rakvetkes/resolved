package org.aki.resolved.datarelated.reaction;

import it.unimi.dsi.fastutil.ints.IntIterator;
import org.aki.resolved.datarelated.blockdata.Constituent;
import org.aki.resolved.datarelated.blockdata.ConstituentTypeIterator;
import org.aki.resolved.datarelated.blockdata.FluidLayer;
import org.apache.logging.log4j.core.util.ObjectArrayIterator;

import java.util.Collection;
import java.util.Iterator;

public class BasicReaction implements InnerReaction, SurfaceReaction{
    Constituent[] ingredient, product;
    float velocity;
    BasicReaction(Constituent[] ingredient, Constituent[]  product, float velocity) {
        this.ingredient = ingredient;
        this.product = product;
        this.velocity = velocity;
    }
    @Override
    public void react(FluidLayer layer) {
        // todo
    }

    @Override
    public IntIterator getReagentIterator() {
        return new ConstituentTypeIterator(new ObjectArrayIterator<Constituent>(ingredient));
    }

    @Override
    public void react(FluidLayer layer1, FluidLayer layer2) {
        // todo
    }
}
