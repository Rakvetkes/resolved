package org.aki.resolved;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.aki.resolved.misc.ResolvedFluid;
import org.aki.resolved.misc.ResolvedFluidBlock;
import org.aki.resolved.chunk.FluidChunk;
import org.aki.resolved.layer.CompClassRegistry;
import org.aki.resolved.layer.ConstituentRegistry;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

public class Registered {

    public static final ComponentKey<FluidChunk> FLUID_DATA;
    public static final Fluid RESOLVED_FLUID;
    public static final Block RESOLVED_FLUID_BLOCK;

    public static final ConstituentRegistry.ConstituentAttributes NULL_ATTRIBUTE;
    public static final int CONSTITUENT_AIR;            // it's supposed that nothing is compatible with air.
    public static final int CONSTITUENT_SOLID;
    public static final int CONSTITUENT_WATER;          // todo springs are broken now
    public static final int CONSTITUENT_LAVA;

    public static class Identifiers {
        public static final Identifier RESOLVED_FLUID = Identifier.of("resolved", "fluid");
        public static final Identifier RESOLVED_FLUID_BLOCK = Identifier.of("resolved", "fluid_block");
        public static final Identifier FLUID_CHUNK_DATA = Identifier.of("resolved", "fluid_chunk_data");
    }

    static {
        FLUID_DATA = ComponentRegistry.getOrCreate(Registered.Identifiers.FLUID_CHUNK_DATA, FluidChunk.class);
        RESOLVED_FLUID = Registry.register(Registries.FLUID, Identifiers.RESOLVED_FLUID, new ResolvedFluid());
        RESOLVED_FLUID_BLOCK = Registry.register(Registries.BLOCK, Identifiers.RESOLVED_FLUID_BLOCK,
                new ResolvedFluidBlock(Blocks.WATER.getSettings()));

        NULL_ATTRIBUTE = new ConstituentRegistry.ConstituentAttributes(0, 0, 0);
        CONSTITUENT_AIR = ConstituentRegistry.REGISTRY.register(Fluids.EMPTY, NULL_ATTRIBUTE.volume(1).density(0).energy(114));
        CONSTITUENT_SOLID = ConstituentRegistry.REGISTRY.register(new Object(), NULL_ATTRIBUTE.volume(1).density(1).energy(0));
        CONSTITUENT_WATER = ConstituentRegistry.REGISTRY.register(Fluids.WATER, NULL_ATTRIBUTE.volume(1).density(1).energy(514));
        CONSTITUENT_LAVA = ConstituentRegistry.REGISTRY.register(Fluids.LAVA, NULL_ATTRIBUTE.volume(1).density(5).energy(1919));

        CompClassRegistry.REGISTRY.createClass(CONSTITUENT_AIR);
        CompClassRegistry.REGISTRY.createClass(CONSTITUENT_SOLID);
        CompClassRegistry.REGISTRY.createClass(CONSTITUENT_WATER);
        CompClassRegistry.REGISTRY.createClass(CONSTITUENT_LAVA);
    }

    public static void registerAll() {}

}
