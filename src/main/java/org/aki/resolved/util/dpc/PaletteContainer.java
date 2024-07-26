package org.aki.resolved.util.dpc;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.PackedIntegerArray;
import org.aki.resolved.util.dpc.allocator.BiArrayAllocator;

public class PaletteContainer<T> implements NbtConvertible {

    private PackedIntegerArray storage;
    private DynamicPalette<T> palette;
    private final DynamicPalette.ValueConverter<T> valueConverter;
    private final static int INITIAL_ELEMENT_BITS = 4;
    private final static DynamicPalette.IDAllocatorProvider DEFAULT_CONTAINER = BiArrayAllocator::new;

    public PaletteContainer(int size, DynamicPalette.ValueConverter<T> valueConverter) {
        storage = new PackedIntegerArray(INITIAL_ELEMENT_BITS, size);
        palette = new DynamicPalette<>(0, DEFAULT_CONTAINER, valueConverter);
        this.valueConverter = valueConverter;
    }

    public PaletteContainer(NbtCompound nbtCompound, DynamicPalette.ValueConverter<T> valueConverter) {
        this.valueConverter = valueConverter;
        readFromNbt(nbtCompound);
    }

    private void resize(int bits) {
        if (bits != storage.getElementBits()) {
            int[] data = new int[storage.getSize()];
            storage.writePaletteIndices(data);
            storage = new PackedIntegerArray(bits, storage.getSize(), data);
        }
    }

    public void expand(int i) {
        int bits = storage.getElementBits();
        while (i >= (1 << bits)) {
            ++bits;
        }
        resize(bits);
    }

    public void shrink() {
        int bits = storage.getElementBits() - 1;
        while (bits >= INITIAL_ELEMENT_BITS && palette.maxValue() < (1 << bits)) {
            --bits;
        }
        resize(bits + 1);
    }

    public void set(int index, T value) {
        palette.recordAddition(value);
        palette.recordRemoval(storage.get(index));
        int i = palette.index(value);
        expand(i);
        storage.set(index, i);
    }

    public T get(int index) {
        return palette.get(storage.get(index));
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        NbtCompound nbtStorage = nbtCompound.getCompound("storage");
        NbtCompound nbtPalette = nbtCompound.getCompound("palette");
        storage = new PackedIntegerArray(nbtStorage.getInt("element_bits"), nbtStorage.getInt("size"),
                nbtStorage.getLongArray("data"));
        palette = new DynamicPalette<>(nbtPalette, DEFAULT_CONTAINER, valueConverter);
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        NbtCompound nbtStorage = new NbtCompound();
        NbtCompound nbtPalette = new NbtCompound();
        nbtStorage.putInt("element_bits", storage.getElementBits());
        nbtStorage.putInt("size", storage.getSize());
        nbtStorage.putLongArray("data", storage.getData());
        palette.writeToNbt(nbtPalette);
        nbtCompound.put("storage", nbtStorage);
        nbtCompound.put("palette", nbtPalette);
    }
}
