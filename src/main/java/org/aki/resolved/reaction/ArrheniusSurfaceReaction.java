package org.aki.resolved.reaction;

import it.unimi.dsi.fastutil.ints.IntConsumer;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import org.aki.resolved.layer.FluidLayer;

public class ArrheniusSurfaceReaction extends ArrheniusReaction implements SurfaceReaction {

    protected final IntIntImmutablePair[] reagents1, reagents2, reagents3, reagents4;
    protected final float velocityMultiplier, activatedEnergy;

    // reagents1(layer1) + reagents2(layer2) = reagents3(layer1) + reagents4(layer2) (invertible)
    public ArrheniusSurfaceReaction(IntIntImmutablePair[] reagents1, IntIntImmutablePair[] reagents2, IntIntImmutablePair[] reagents3, IntIntImmutablePair[] reagents4, float velocityMultiplier, float activatedEnergy) {
        this.reagents1 = reagents1;
        this.reagents2 = reagents2;
        this.reagents3 = reagents3;
        this.reagents4 = reagents4;
        this.velocityMultiplier = velocityMultiplier;
        this.activatedEnergy = activatedEnergy / 2.0f;
    }

    @Override
    public void forEachReagent(IntConsumer consumer) {
        for (IntIntImmutablePair intIntImmutablePair : reagents1) {
            consumer.accept(intIntImmutablePair.leftInt());
        }
        for (IntIntImmutablePair intIntImmutablePair : reagents2) {
            consumer.accept(intIntImmutablePair.rightInt());
        }
        for (IntIntImmutablePair intIntImmutablePair : reagents3) {
            consumer.accept(intIntImmutablePair.leftInt());
        }
        for (IntIntImmutablePair intIntImmutablePair : reagents4) {
            consumer.accept(intIntImmutablePair.rightInt());
        }
    }

    @Override
    public void react(FluidLayer layer1, FluidLayer layer2, float temp) {
        float leftMultiplier = collectMultiplier1(reagents1, layer1) * collectMultiplier1(reagents2, layer2)
                * collectMultiplier2(reagents1, activatedEnergy, temp) * collectMultiplier2(reagents2, activatedEnergy, temp);
        float rightMultiplier = collectMultiplier1(reagents3, layer1) * collectMultiplier1(reagents4, layer2)
                * collectMultiplier2(reagents3, activatedEnergy, temp) * collectMultiplier2(reagents4, activatedEnergy, temp);
        float progress = (leftMultiplier - rightMultiplier) * velocityMultiplier;
        if (progress >= CUT_DOWN_VALUE) {
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

    }

}
