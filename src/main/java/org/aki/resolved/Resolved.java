package org.aki.resolved;

import net.fabricmc.api.ModInitializer;
import org.aki.resolved.debug.CommandGetBlock;

public class Resolved implements ModInitializer {

    @Override
    public void onInitialize() {
        Registered.registerAll();
        CommandGetBlock.onInitialize();
    }

}
