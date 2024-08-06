package org.aki.resolved.reaction;

import it.unimi.dsi.fastutil.ints.IntConsumer;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import org.aki.resolved.layer.FluidLayer;

public class BasicSurfaceReaction extends BasicReaction implements SurfaceReaction {

    protected final IntIntImmutablePair[] reagents1, reagents2, reagents3, reagents4;

    // reagents1(layer1) + reagents2(layer2) => reagents3(layer1) + reagents4(layer2)
    protected BasicSurfaceReaction(IntIntImmutablePair[] reagents1, IntIntImmutablePair[] reagents2, IntIntImmutablePair[] reagents3, IntIntImmutablePair[] reagents4, float velocity) {
        super(velocity);
        this.reagents1 = reagents1;
        this.reagents2 = reagents2;
        this.reagents3 = reagents3;
        this.reagents4 = reagents4;
    }

    @Override
    public void react(FluidLayer layer1, FluidLayer layer2, float temp) {
        float progress = collectMultiplier(temp) * velocity;
        for (IntIntImmutablePair r : reagents1) {
            progress = Math.min(progress, layer1.amount(r.leftInt()) / r.rightInt());
        }
        for (IntIntImmutablePair r : reagents2) {
            progress = Math.min(progress, layer2.amount(r.leftInt()) / r.rightInt());
        }
        for (IntIntImmutablePair r : reagents1) {
            layer1.absorb(r.leftInt(), -progress * r.rightInt());
        }
        for (IntIntImmutablePair r : reagents2) {
            layer2.absorb(r.leftInt(), -progress * r.rightInt());
        }
        for (IntIntImmutablePair r : reagents3) {
            layer1.absorb(r.leftInt(), progress * r.rightInt());
        }
        for (IntIntImmutablePair r : reagents4) {
            layer2.absorb(r.leftInt(), progress * r.rightInt());
        }
    }

    @Override
    public void forEachReagent1(IntConsumer consumer) {
        for (IntIntImmutablePair intIntImmutablePair : reagents1) {
            consumer.accept(intIntImmutablePair.leftInt());
        }
        for (IntIntImmutablePair intIntImmutablePair : reagents2) {
            consumer.accept(intIntImmutablePair.leftInt());
        }
    }

    @Override
    public void forEachReagent2(IntConsumer consumer) {}
}
