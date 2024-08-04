package org.aki.resolved.chunk;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.EmptyPaletteStorage;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.collection.PaletteStorage;
import org.aki.resolved.chunk.idallocator.BiArrayAllocator;

public class PaletteContainer<T> implements NbtConvertible {

    private final static DynamicPalette.IDAllocatorProvider DEFAULT_CONTAINER = BiArrayAllocator::new;
    private PaletteStorage storage;
    private DynamicPalette<T> palette;
    private final DynamicPalette.ValueConverter<T> valueConverter;

    public PaletteContainer(int size, T nullObject, DynamicPalette.ValueConverter<T> valueConverter) {
        storage = new EmptyPaletteStorage(size);
        palette = new DynamicPalette<>(0, DEFAULT_CONTAINER, valueConverter);
        this.valueConverter = valueConverter;

        palette.recordAddition(nullObject);     // 0 should always be the index of null data
    }

    public PaletteContainer(NbtCompound nbtCompound, DynamicPalette.ValueConverter<T> valueConverter) {
        this.valueConverter = valueConverter;
        readFromNbt(nbtCompound);
    }

    private void resetBits(int bits) {
        if (bits != storage.getElementBits()) {
            if (bits == 0) {
                storage = new EmptyPaletteStorage(storage.getSize());
            } else {
                int[] data = new int[storage.getSize()];
                storage.writePaletteIndices(data);
                storage = new PackedIntegerArray(bits, storage.getSize(), data);
            }
        }
    }

    public void expand(int i) {
        int bits = storage.getElementBits();
        while (i >= (1 << bits)) {
            ++bits;
        }
        resetBits(bits);
    }

    public void shrink() {
        int bits = storage.getElementBits() - 1;
        while (bits >= 0 && palette.maxValue() < (1 << bits)) {
            --bits;
        }
        resetBits(bits + 1);
    }

    public void set(int index, T value) {
        int originalId = storage.get(index);
        if (originalId != 0) {
            palette.recordRemoval(storage.get(index));
        }
        if (palette.index(value) != 0) {
            palette.recordAddition(value);
        }
        int i = palette.index(value);
        expand(i);
        storage.set(index, i);
    }

    public T get(int index) {
        return palette.get(storage.get(index));
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        int elementBits = nbtCompound.getInt("element_bits");
        storage = elementBits == 0 ? new EmptyPaletteStorage(nbtCompound.getInt("size")) :
                new PackedIntegerArray(elementBits, nbtCompound.getInt("size"), nbtCompound.getLongArray("data"));
        palette = new DynamicPalette<>(nbtCompound.getCompound("palette"), DEFAULT_CONTAINER, valueConverter);
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putInt("size", storage.getSize());
        if (storage.getElementBits() != 0) {
            nbtCompound.putInt("element_bits", storage.getElementBits());
            nbtCompound.putLongArray("data", storage.getData());
        }
        NbtCompound nbtPalette = new NbtCompound();
        palette.writeToNbt(nbtPalette);
        nbtCompound.put("palette", nbtPalette);
    }
}
