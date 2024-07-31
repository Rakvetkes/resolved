package org.aki.resolved.fluiddata.blockdata;

import net.minecraft.util.math.MathHelper;
import org.aki.resolved.Registered;
import org.aki.resolved.fluiddata.blockdata.reaction.CompatibilityRegistry;
import org.aki.resolved.fluiddata.blockdata.reaction.ConstituentRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class FluidLayer implements Iterable<FluidLayer.Constituent> {

    private final LinkedList<Constituent> constituents;
    private float totalVolume;
    private float density;

    private FluidLayer() {
        constituents = new LinkedList<>();
        totalVolume = density = 0.0f;
    }

    public FluidLayer(Constituent base) {
        constituents = new LinkedList<>();
        constituents.add(base);
        totalVolume = base.getVolume();
        density = base.getDensity();
    }

    private interface ConstituentComparator {
        boolean compare(int id1, int id2, float density1, float density2);
    }

    private void add(Constituent constituent, ListIterator<Constituent> it, ListHelper.FloatComparator comparator) {
        float volume = constituent.getVolume();
        float density = constituent.getDensity();
        ConstituentComparator consComparator = (id1, id2, density1, density2)
                -> MathHelper.approximatelyEquals(density1, density2) ? comparator.compare(id1, id2)
                : comparator.compare(density1, density2);
        boolean flag = false;
        while (!flag && it.hasNext()) {
            Constituent j = it.next();
            if (j.consId == constituent.consId) {
                it.set(j.combine(constituent));
                flag = true;
            } else if (consComparator.compare(j.consId(), constituent.consId(), j.getDensity(), density)) {
                it.previous();
                it.add(constituent);
                flag = true;
            }
        }
        if (!flag) {
            it.add(constituent);
        }
        this.density = (this.density * this.totalVolume + density * volume) / (this.totalVolume + volume);
        this.totalVolume += constituent.getVolume();
    }

    public void addFromFront(Constituent constituent) {
        add(constituent, constituents.listIterator(), (a, b) -> a > b);
    }

    public void addFromBack(Constituent constituent) {
        add(constituent, new ListHelper.ReversedListIterator<>(constituents), (a, b) -> a <= b);
    }

    public void add(Constituent constituent) {
        if (constituent.getDensity() > this.density) {
            addFromBack(constituent);
        } else {
            addFromFront(constituent);
        }
    }

    public ListIterator<Constituent> getIterator() {
        return constituents.listIterator();
    }

    public float getVolume() {
        return totalVolume;
    }

    public float getDensity() {
        return density;
    }

    public boolean isCompatible(Constituent constituent) {
        return CompatibilityRegistry.REGISTRY.checkCompatibility(constituents.getFirst().consId(), constituent.consId());
    }

    public boolean isCompatible(FluidLayer layer) {
        return layer.isCompatible(constituents.getFirst());
    }

    public boolean isAir() {
        return constituents.getFirst().consId() == Registered.CONSTITUENT_AIR;
    }

    public FluidLayer sliceByVolume(float volume) {
        ListIterator<Constituent> it = constituents.listIterator();
        FluidLayer sliced = new FluidLayer();
        float proportion = volume / totalVolume;
        while (it.hasNext()) {
            Constituent j = it.next();
            sliced.addFromBack(j.sliceByProportion(proportion));
            it.set(j.sliceByProportion(1.0f - proportion));
        }
        return sliced;
    }

    public void combine(FluidLayer layer) {
        for (Constituent constituent : layer.constituents) {
            this.add(constituent);
        }
    }

    @NotNull @Override
    public Iterator<Constituent> iterator() {
        return constituents.listIterator();
    }

    @Override
    public int hashCode() {
        return constituents.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FluidLayer && hashCode() == o.hashCode();
    }

    public record Constituent(int consId, float amount) {

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

    }

}
