package org.aki.resolved.reaction;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import org.aki.resolved.layer.ConstituentRegistry;
import org.aki.resolved.layer.FluidLayer;

public class ArrheniusReaction {

    protected static final float CUT_DOWN_VALUE = 0.01f;
    protected final float velocityMultiplier, activatedEnergy;

    protected ArrheniusReaction(float velocityMultiplier, float activatedEnergy) {
        this.velocityMultiplier = velocityMultiplier;
        this.activatedEnergy = activatedEnergy;
    }

    protected float collectMultiplier1(IntIntImmutablePair[] reagents, FluidLayer layer) {
        float retValue = 1.0f;
        for (IntIntImmutablePair r : reagents) {
            retValue *= (float) Math.pow(layer.amount(r.leftInt()) / layer.getVolume(), r.rightInt());
        }
        return retValue;
    }

    protected float collectMultiplier2(IntIntImmutablePair[] reagents, float temp) {
        float activationEnergy = activatedEnergy;
        for (IntIntImmutablePair r : reagents) {
            activationEnergy -= ConstituentRegistry.REGISTRY.getAttributes(r.leftInt()).energy() * r.rightInt();
        }
        return (float) Math.exp(-activationEnergy / temp);
    }

}
