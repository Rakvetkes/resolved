package org.aki.resolved.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.BlendingData;
import org.aki.resolved.Registered;
import org.aki.resolved.layer.FluidLayerSet;
import org.aki.resolved.misc.DataSyncHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldChunk.class)
public abstract class MixinWorldChunk extends Chunk {

    public MixinWorldChunk(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable ChunkSection[] sectionArray, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biomeRegistry, inhabitedTime, sectionArray, blendingData);
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onStateReplaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onStateReplaced(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir, int i, ChunkSection chunkSection, boolean bl, int j, int k, int l, BlockState blockState, Block block, boolean bl2, boolean bl3) {
        Registered.FLUID_DATA.get(this).setFluidData(j, i, l, FluidLayerSet.NULL_LAYER_SET);
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onBlockAdded(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onBlockAdded(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir, int i, ChunkSection chunkSection, boolean bl, int j, int k, int l, BlockState blockState, Block block) {
        DataSyncHelper.onBlockStateGenerated(this, state, j, i, l, false);
    }

}
