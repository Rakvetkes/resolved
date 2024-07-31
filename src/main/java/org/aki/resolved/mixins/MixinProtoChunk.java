package org.aki.resolved.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.chunk.BlendingData;
import org.aki.resolved.Registered;
import org.aki.resolved.fluiddata.FluidBlockData;
import org.aki.resolved.fluiddata.blockdata.reaction.ConstituentRegistry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ProtoChunk.class)
public abstract class MixinProtoChunk extends Chunk {

    public MixinProtoChunk(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable ChunkSection[] sectionArray, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biomeRegistry, inhabitedTime, sectionArray, blendingData);
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockState;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void insertedCode1(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir, int i, int j, int k, int l, ChunkSection chunkSection, boolean bl, int m, int n, int o) {
        if (ConstituentRegistry.REGISTRY.get(state.getFluidState().getFluid()) > 0) {
            // todo this should be adjusted if there's a waterlogged block
            Registered.FLUID_DATA.get(this).setFluidData(m, j, o, FluidBlockData.getFromFluid(state.getFluidState().getFluid()));
        }
    }

    @Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockState;"))
    public BlockState insertedCode2(ChunkSection instance, int x, int y, int z, BlockState state) {
        if (ConstituentRegistry.REGISTRY.get(state.getFluidState().getFluid()) > 0) {
            // todo this should be adjusted if there's a waterlogged block
            state = Registered.RESOLVED_FLUID_BLOCK.getDefaultState();
        }
        return instance.setBlockState(x, y, z, state);
    }

}
