package org.aki.resolved.misc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.aki.resolved.Registered;
import org.aki.resolved.layer.FluidLayerSet;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandGetFluidData {

    public static void onInitialize() {
        CommandRegistrationCallback.EVENT.register(CommandGetFluidData::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(literal("getfluiddata")
                .then(argument("blockPos", BlockPosArgumentType.blockPos())
                        .executes(context -> execute(context, BlockPosArgumentType.getBlockPos(context, "blockPos")))));
    }

    private static int execute(CommandContext<ServerCommandSource> context, BlockPos blockPos) {
        ServerCommandSource source = context.getSource();
        FluidLayerSet data = Registered.FLUID_DATA.get(context.getSource().getWorld().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4))
                        .getFluidData(blockPos.getX() & 15, blockPos.getY(), blockPos.getZ() & 15);
        final String[] output = new String[1];
        output[0] = "Fluid at " + blockPos + " is ";
        data.forEachLayer(layer -> {
            output[0] += "[layer: ";
            layer.forEachConstituent((integer, aFloat) -> output[0] += "(" + integer + "," + aFloat + ") ");
            output[0] += "]";
        });
        source.sendFeedback(() -> Text.literal(output[0]), false);
        return 1;
    }

}
