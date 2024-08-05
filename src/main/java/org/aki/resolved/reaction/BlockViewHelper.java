package org.aki.resolved.reaction;

import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

public final class BlockViewHelper {
    private static World tempWorld;
    public static World getWorld(BlockRenderView view) {
        if (tempWorld == view)
            return tempWorld;
        if (view.getClass().equals(ChunkRendererRegion.class)) {
            try {
                return tempWorld = (World) view.getClass().getDeclaredField("world").get(view);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException();
    }
}
