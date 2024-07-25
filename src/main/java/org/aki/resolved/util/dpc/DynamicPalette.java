package org.aki.resolved.util.dpc;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.collection.Int2ObjectBiMap;
import org.aki.resolved.util.dpc.mex.MexContainer;

import java.util.function.Predicate;

// a palette is a map by nature.
public class DynamicPalette<T, C extends MexContainer & NbtConvertible> implements NbtConvertible {

    private Int2ObjectBiMap<T> palette;
    private final C counter;
    private final ValueConverter<T> valueConverter;           // this object links value objects to their nbt forms

    private DynamicPalette(Int2ObjectBiMap<T> palette, C counter, ValueConverter<T> valueConverter) {
        this.palette = palette;
        this.counter = counter;
        this.valueConverter = valueConverter;
    }

    public DynamicPalette(int bits, MexContainerProvider<C> counterProvider, ValueConverter<T> valueConverter) {
        this.palette = Int2ObjectBiMap.create(1 << bits);
        this.counter = counterProvider.createMexContainer();
        this.valueConverter = valueConverter;
    }

    public DynamicPalette(NbtCompound nbtCompound, MexContainerProvider<C> counterProvider, ValueConverter<T> valueConverter) {
        this.counter = counterProvider.createMexContainer();
        this.valueConverter = valueConverter;
        this.readFromNbt(nbtCompound);
    }

    public int index(T object) {
        if (palette.contains(object)) {
            int id = palette.getRawId(object);
            if (counter.count(id) > 0) {
                return id;
            }
        }
        return -1;
    }

    public void recordAddition(T object) {
        int id = index(object);
        if (index(object) == -1) {
            id = counter.mex();
            palette.put(object, id);
        }
        counter.put(id);
    }

    public void recordRemoval(T object) {
        int id = index(object);
        if (id != -1) {
            counter.remove(id);
        }
    }

    public boolean hasAny(Predicate predicate) {
        for (int i = 0; i <= counter.maxValue(); ++i) {
            if (counter.count(i) > 0 && predicate.test(palette.get(i))) {
                return true;
            }
        }
        return false;
    }

    public T get(int id) {
        if (counter.count(id) > 0) {
            return palette.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        NbtCompound nbtPalette = (NbtCompound) nbtCompound.get("palette");
        NbtCompound nbtCounter = (NbtCompound) nbtCompound.get("counter");

        counter.readFromNbt(nbtCounter);
        this.palette = Int2ObjectBiMap.create(counter.maxValue());

        for (int i = 0; i <= counter.maxValue(); ++i) {
            if (nbtPalette.contains(String.format("%d", i))) {
                NbtElement nbtObject = nbtPalette.get(String.format("%d", i));
                this.palette.put(valueConverter.getValue(nbtObject), i);
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        NbtCompound nbtPalette = new NbtCompound();
        NbtCompound nbtCounter = new NbtCompound();

        for (int i = 0; i <= counter.maxValue(); ++i) {
            if (counter.count(i) > 0) {
                nbtPalette.put(String.format("%d", i), valueConverter.getNbt(palette.get(i)));
            }
        }
        this.counter.writeToNbt(nbtCounter);

        nbtCompound.put("palette", nbtPalette);
        nbtCompound.put("counter", nbtCounter);
    }

    public int getSize() {
        return counter.valueCount();
    }

    public DynamicPalette<T, C> copy() {
        return new DynamicPalette(palette.copy(), counter.copy(), valueConverter);
    }

    public interface MexContainerProvider<C> {
        C createMexContainer();
    }

    public interface ValueConverter<T> {
        T getValue(NbtElement nbtElement);
        NbtElement getNbt(T value);
    }

}
