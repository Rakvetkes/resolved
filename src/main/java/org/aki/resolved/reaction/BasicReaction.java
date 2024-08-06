package org.aki.resolved.reaction;

public class BasicReaction {

    protected final float velocity;

    protected BasicReaction(float velocity) {
        this.velocity = velocity;
    }

    protected float collectMultiplier(float temp) {
        return (float) Math.exp(-1.0f / temp);
    }

}
