package org.aki.resolved.misc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class CommandGetBlock {

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register(CommandGetBlock::registerCommand);
    }

    private static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(literal("getblock")
                .then(argument("blockPos", BlockPosArgumentType.blockPos())
                        .executes(context -> execute(context, BlockPosArgumentType.getBlockPos(context, "blockPos")))));
    }

    private static int execute(CommandContext<ServerCommandSource> context, BlockPos blockPos) {
        ServerCommandSource source = context.getSource();
        BlockState blockState = source.getWorld().getBlockState(blockPos);
        source.sendFeedback(() -> Text.literal("Block at " + blockPos.toString() + " is "
                + blockState.getBlock().getName()), false);
        return 1;
    }

}
