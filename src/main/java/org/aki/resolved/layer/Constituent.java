package org.aki.resolved.layer;

import net.minecraft.util.math.MathHelper;

public record Constituent(int consId, float amount) implements ListHelper.Copyable<Constituent> {

    public float getVolume() {
        return ConstituentRegistry.REGISTRY.getAttributes(consId).volume() * amount;
    }

    public float getDensity() {
        return ConstituentRegistry.REGISTRY.getAttributes(consId).density();
    }

    public Constituent sliceByProportion(float point) {
        return new Constituent(consId, point * amount);
    }

    public Constituent combine(Constituent constituent) {
        if (constituent.consId != this.consId) {
            throw new IllegalArgumentException();
        }
        return new Constituent(consId, this.amount + constituent.amount);
    }

    @Override
    public int hashCode() {
        return (int) (amount * 1e5) + consId;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Constituent && consId == ((Constituent) o).consId
                && MathHelper.approximatelyEquals(amount, ((Constituent) o).amount);
    }

    @Override
    public Constituent copy() {
        return new Constituent(consId, amount);
    }
}
