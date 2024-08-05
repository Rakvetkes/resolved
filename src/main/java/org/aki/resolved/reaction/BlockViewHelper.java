package org.aki.resolved.reaction;

import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

public final class BlockViewHelper {
    private static BlockRenderView tempChunk;
    private static World tempChunkWorld;
    public static World getWorld(BlockRenderView view) {
        if (tempChunk == view)
            return tempChunkWorld;
        if (view.getClass().equals(ChunkRendererRegion.class)) {
            try {
                tempChunkWorld = (World) view.getClass().getDeclaredField("world").get(view);
                tempChunk = view;
                return tempChunkWorld;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException();
    }
}
