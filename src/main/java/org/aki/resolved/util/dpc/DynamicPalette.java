package org.aki.resolved.util.dpc;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.collection.Int2ObjectBiMap;
import org.aki.resolved.util.dpc.allocator.IdAllocator;

import java.util.function.Predicate;

// a palette is a map by nature.
public class DynamicPalette<T> implements NbtConvertible {

    private Int2ObjectBiMap<T> palette;
    private IdAllocator counter;
    private final IDAllocatorProvider counterProvider;
    private final ValueConverter<T> valueConverter;           // this object links value objects to their nbt forms

    private DynamicPalette(Int2ObjectBiMap<T> palette, IdAllocator counter, IDAllocatorProvider counterProvider, ValueConverter<T> valueConverter) {
        this.palette = palette;
        this.counter = counter;
        this.counterProvider = counterProvider;
        this.valueConverter = valueConverter;
    }

    public DynamicPalette(int bits, IDAllocatorProvider counterProvider, ValueConverter<T> valueConverter) {
        this.palette = Int2ObjectBiMap.create(1 << bits);
        this.counter = counterProvider.createIDAllocator();
        this.counterProvider = counterProvider;
        this.valueConverter = valueConverter;
    }

    public DynamicPalette(NbtCompound nbtCompound, IDAllocatorProvider counterProvider, ValueConverter<T> valueConverter) {
        this.valueConverter = valueConverter;
        this.counterProvider = counterProvider;
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
        if (id == -1) {
            id = counter.newId();
            palette.put(object, id);
        }
        counter.put(id);
    }

    public void recordRemoval(int id) {
        counter.remove(id);
    }

    public boolean hasAny(Predicate<T> predicate) {
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

    public int maxValue() {
        return counter.maxValue();
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        NbtCompound nbtPalette = nbtCompound.getCompound("palette");
        NbtCompound nbtCounter = nbtCompound.getCompound("counter");

        int maxValue = nbtCounter.getInt("maxValue");
        this.palette = Int2ObjectBiMap.create(maxValue);
        this.counter = counterProvider.createIDAllocator();
        for (int i = 0; i <= maxValue; ++i) {
            if (nbtPalette.contains(String.format("%d", i))) {
                NbtElement nbtObject = nbtPalette.get(String.format("%d", i));
                this.palette.put(valueConverter.getValue(nbtObject), i);
                this.counter.put(i, nbtCounter.getInt(String.format("%d", i)));
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        NbtCompound nbtPalette = new NbtCompound();
        NbtCompound nbtCounter = new NbtCompound();

        nbtCounter.putInt("maxValue", counter.maxValue());
        for (int i = 0; i <= counter.maxValue(); ++i) {
            if (counter.count(i) > 0) {
                nbtPalette.put(String.format("%d", i), valueConverter.getNbt(palette.get(i)));
                nbtCounter.putInt(String.format("%d", i), counter.count(i));
            }
        }

        nbtCompound.put("palette", nbtPalette);
        nbtCompound.put("counter", nbtCounter);
    }

    public int getSize() {
        return counter.valueCount();
    }

    public DynamicPalette<T> copy() {
        return new DynamicPalette<>(palette.copy(), counter.copy(), counterProvider, valueConverter);
    }

    public interface IDAllocatorProvider {
        IdAllocator createIDAllocator();
    }

    public interface ValueConverter<T> {
        T getValue(NbtElement nbtElement);
        NbtElement getNbt(T value);
    }

}
