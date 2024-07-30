package org.aki.resolved;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.aki.resolved.fluidblock.ResolvedFluid;
import org.aki.resolved.fluidblock.ResolvedFluidBlock;
import org.aki.resolved.fluiddata.FluidChunkData;
import org.aki.resolved.fluiddata.blockdata.reaction.ConstituentRegistry;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

public class Registered {

    public static final ComponentKey<FluidChunkData> FLUID_DATA = ComponentRegistry
            .getOrCreate(Registered.Identifiers.FLUID_CHUNK_DATA, FluidChunkData.class);
    public static final Fluid RESOLVED_FLUID = Registry.register(Registries.FLUID,
            Identifiers.RESOLVED_FLUID, new ResolvedFluid());
    public static final Block RESOLVED_FLUID_BLOCK = Registry.register(Registries.BLOCK,
            Identifiers.RESOLVED_FLUID_BLOCK, new ResolvedFluidBlock(Blocks.WATER.getSettings()));

    public static class Constituents {
        public static final ConstituentRegistry.ConstituentAttributes NULL_ATTRIBUTE = new ConstituentRegistry.ConstituentAttributes(0, 0);
        public static final int WATER = ConstituentRegistry.REGISTRY.register(Fluids.WATER, NULL_ATTRIBUTE.volume(1).density(1));
        public static final int LAVA = ConstituentRegistry.REGISTRY.register(Fluids.LAVA, NULL_ATTRIBUTE.volume(1).density(5));
    }

    public static class Identifiers {
        public static final Identifier RESOLVED_FLUID = Identifier.of("resolved", "fluid");
        public static final Identifier RESOLVED_FLUID_BLOCK = Identifier.of("resolved", "fluid_block");
        public static final Identifier FLUID_CHUNK_DATA = Identifier.of("resolved", "fluid_chunk_data");
    }

    public static class Tags {
        public static final TagKey<Fluid> RESOLVED_FLUID = TagKey.of(RegistryKeys.FLUID, Identifiers.RESOLVED_FLUID);
    }

}
