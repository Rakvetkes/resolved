package org.aki.resolved.reaction;

import it.unimi.dsi.fastutil.ints.IntConsumer;
import org.aki.resolved.layer.FluidLayer;

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
    public void forEachReagent(IntConsumer consumer) {
        for (int i = 0; i < ingredient.length; ++i) {
            consumer.accept(ingredient[i].consId());
        }
    }

    @Override
    public void react(FluidLayer layer1, FluidLayer layer2) {
        // todo
    }
}
