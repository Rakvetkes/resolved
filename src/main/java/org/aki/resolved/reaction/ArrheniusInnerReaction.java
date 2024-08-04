package org.aki.resolved.reaction;

import it.unimi.dsi.fastutil.ints.IntConsumer;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import org.aki.resolved.layer.FluidLayer;

public class ArrheniusInnerReaction extends ArrheniusReaction implements InnerReaction {

    protected final IntIntImmutablePair[] reagents1, reagents2;
    protected final float velocityMultiplier, activatedEnergy;

    public ArrheniusInnerReaction(IntIntImmutablePair[] reagents1, IntIntImmutablePair[] reagents2, float velocityMultiplier, float activatedEnergy) {
        this.reagents1 = reagents1;
        this.reagents2 = reagents2;
        this.velocityMultiplier = velocityMultiplier;
        this.activatedEnergy = activatedEnergy;
    }

    @Override
    public void forEachReagent(IntConsumer consumer) {
        for (IntIntImmutablePair intIntImmutablePair : reagents1) {
            consumer.accept(intIntImmutablePair.leftInt());
        }
        for (IntIntImmutablePair intIntImmutablePair : reagents2) {
            consumer.accept(intIntImmutablePair.rightInt());
        }
    }

    @Override
    public void react(FluidLayer layer, float temp) {
        float leftMultiplier = collectMultiplier1(reagents1, layer) * collectMultiplier2(reagents1, activatedEnergy, temp);
        float rightMultiplier = collectMultiplier1(reagents2, layer) * collectMultiplier2(reagents2, activatedEnergy, temp);
        float progress = (leftMultiplier - rightMultiplier) * velocityMultiplier;
        if (progress >= CUT_DOWN_VALUE) {
            for (IntIntImmutablePair r : reagents1) {
                layer.absorb(r.leftInt(), -progress * r.rightInt());
            }
            for (IntIntImmutablePair r : reagents2) {
                layer.absorb(r.leftInt(), progress * r.rightInt());
            }
        }
    }

}
