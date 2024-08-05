package org.aki.resolved.chunk;

import com.google.common.collect.HashBiMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.aki.resolved.chunk.idallocator.IdAllocator;

import java.util.function.Predicate;

// a palette is a map by nature.
public class DynamicPalette<T> implements NbtConvertible {

    private HashBiMap<Integer, T> palette;
    private IdAllocator counter;
    private final IDAllocatorProvider counterProvider;
    private final ValueConverter<T> valueConverter;           // this object links value objects to their nbt forms

    private DynamicPalette(HashBiMap<Integer, T> palette, IdAllocator counter, IDAllocatorProvider counterProvider, ValueConverter<T> valueConverter) {
        this.palette = palette;
        this.counter = counter;
        this.counterProvider = counterProvider;
        this.valueConverter = valueConverter;

    }

    public DynamicPalette(int bits, IDAllocatorProvider counterProvider, ValueConverter<T> valueConverter) {
        this.palette = HashBiMap.create(1 << bits);
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
        Integer id = palette.inverse().get(object);
        return id != null && counter.count(id) > 0 ? id : -1;
    }

    public void recordAddition(T object) {
        int id = index(object);
        if (id == -1) {
            id = counter.newId();
            palette.put(id, object);
        }
        counter.put(id);
    }

    public void recordRemoval(int id) {
        counter.remove(id);
        if (counter.count(id) == 0) {
            palette.remove(id);
        }
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
        int maxValue = nbtCompound.getInt("maxValue");
        this.palette = HashBiMap.create(maxValue + 1);
        this.counter = counterProvider.createIDAllocator();
        for (int i = 0; i <= maxValue; ++i) {
            if (nbtCompound.contains(String.format("%d", i))) {
                NbtCompound nbtItem = nbtCompound.getCompound(String.format("%d", i));
                this.palette.put(i, valueConverter.getValue(nbtItem));
                this.counter.put(i, nbtItem.getInt("count"));
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putInt("maxValue", counter.maxValue());
        for (int i = 0; i <= counter.maxValue(); ++i) {
            if (counter.count(i) > 0) {
                NbtCompound nbtItem = (NbtCompound) valueConverter.getNbt(palette.get(i));
                nbtItem.putInt("count", counter.count(i));
                nbtCompound.put(String.format("%d", i), nbtItem);
            }
        }
    }

    public int getSize() {
        return counter.valueCount();
    }

    public DynamicPalette<T> copy() {
        return new DynamicPalette<>(HashBiMap.create(palette), counter.copy(), counterProvider, valueConverter);
    }

    public interface IDAllocatorProvider {
        IdAllocator createIDAllocator();
    }

    public interface ValueConverter<T> {
        T getValue(NbtElement nbtElement);
        NbtElement getNbt(T value);
    }

}
