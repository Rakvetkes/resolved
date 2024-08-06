package org.aki.resolved.reaction;

import it.unimi.dsi.fastutil.ints.IntConsumer;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import org.aki.resolved.layer.FluidLayer;

public class BasicInnerReaction extends BasicReaction implements InnerReaction {

    protected final IntIntImmutablePair[] reagents1, reagents2;

    public BasicInnerReaction(IntIntImmutablePair[] reagents1, IntIntImmutablePair[] reagents2, float velocity) {
        super(velocity);
        this.reagents1 = reagents1;
        this.reagents2 = reagents2;
    }

    @Override
    public void react(FluidLayer layer, float temp) {
        float progress = collectMultiplier(temp) * velocity;
        for (IntIntImmutablePair r : reagents1) {
            progress = Math.min(progress, layer.amount(r.leftInt()) / r.rightInt());
        }
        for (IntIntImmutablePair r : reagents1) {
            layer.absorb(r.leftInt(), -progress * r.rightInt());
        }
        for (IntIntImmutablePair r : reagents2) {
            layer.absorb(r.leftInt(), progress * r.rightInt());
        }
    }

    @Override
    public void forEachReagent1(IntConsumer consumer) {
        for (IntIntImmutablePair intIntImmutablePair : reagents1) {
            consumer.accept(intIntImmutablePair.leftInt());
        }
    }

    @Override
    public void forEachReagent2(IntConsumer consumer) {}

}
