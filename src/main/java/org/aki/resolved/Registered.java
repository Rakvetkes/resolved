package org.aki.resolved;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.aki.resolved.fluidblock.ResolvedFluid;
import org.aki.resolved.fluidblock.ResolvedFluidBlock;

public class Registered {

    public static Fluid RESOLVED_FLUID = Registry.register(Registries.FLUID,
            Identifiers.RESOLVED_FLUID, new ResolvedFluid());
    public static Block RESOLVED_FLUID_BLOCK = Registry.register(Registries.BLOCK,
            Identifiers.RESOLVED_FLUID_BLOCK, new ResolvedFluidBlock(Blocks.WATER.getSettings()));

    public static class Identifiers {
        public static Identifier RESOLVED_FLUID = Identifier.of("resolved", "fluid");
        public static Identifier RESOLVED_FLUID_BLOCK = Identifier.of("resolved", "fluid_block");
        public static Identifier FLUID_CHUNK_DATA = Identifier.of("resolved", "fluid_chunk_data");
    }

    public static class Tags {
        public static TagKey<Fluid> RESOLVED_FLUID = TagKey.of(RegistryKeys.FLUID, Identifiers.RESOLVED_FLUID);
    }

}
