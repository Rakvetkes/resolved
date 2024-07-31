package org.aki.resolved.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.aki.resolved.Registered;
import org.aki.resolved.fluiddata.FluidBlockData;
import org.aki.resolved.fluiddata.blockdata.reaction.ConstituentRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NoiseChunkGenerator.class)
public abstract class MixinNoiseChunkGenerator {

    @Inject(method = "populateNoise(Lnet/minecraft/world/gen/chunk/Blender;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/world/chunk/Chunk;II)Lnet/minecraft/world/chunk/Chunk;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void insertedCode1(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk, int minimumCellY, int cellHeight, CallbackInfoReturnable<Chunk> cir, ChunkNoiseSampler chunkNoiseSampler, Heightmap heightmap, Heightmap heightmap2, ChunkPos chunkPos, int i, int j, AquiferSampler aquiferSampler, BlockPos.Mutable mutable, int k, int l, int m, int n, int o, int p, int q, ChunkSection chunkSection, int r, int s, int t, int u, int v, double d, int w, int x, int y, double e, int z, int aa, int ab, double f, BlockState blockState) {
        if (ConstituentRegistry.REGISTRY.get(blockState.getFluidState().getFluid()) > 0) {
            Registered.FLUID_DATA.get(chunk).setFluidData(y, t, ab, FluidBlockData.getFromFluid(blockState.getFluidState().getFluid()));
        }
    }

    @Redirect(method = "populateNoise(Lnet/minecraft/world/gen/chunk/Blender;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/world/chunk/Chunk;II)Lnet/minecraft/world/chunk/Chunk;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;"))
    public BlockState insertedCode2(ChunkSection instance, int x, int y, int z, BlockState state, boolean lock) {
        if (ConstituentRegistry.REGISTRY.get(state.getFluidState().getFluid()) > 0) {
            state = Registered.RESOLVED_FLUID_BLOCK.getDefaultState();
        }
        return instance.setBlockState(x, y, z, state, lock);
    }

}
