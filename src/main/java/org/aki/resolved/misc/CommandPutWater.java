package org.aki.resolved.misc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import org.aki.resolved.Registered;
import org.aki.resolved.chunk.FluidChunk;
import org.aki.resolved.layer.ConstituentRegistry;
import org.aki.resolved.layer.FluidLayer;
import org.aki.resolved.layer.FluidLayerSet;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandPutWater {

    public static void onInitialize() {
        CommandRegistrationCallback.EVENT.register(CommandPutWater::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(literal("putwater")
                .then(argument("blockPos", BlockPosArgumentType.blockPos())
                        .then(argument("amount", FloatArgumentType.floatArg())
                        .executes(context -> execute(context, BlockPosArgumentType.getBlockPos(context, "blockPos"), FloatArgumentType.getFloat(context, "amount"))))));
    }

    private static int execute(CommandContext<ServerCommandSource> context, BlockPos blockPos, float amount) {
        FluidChunk chunk = Registered.FLUID_DATA.get(context.getSource().getWorld().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4));
        FluidLayerSet data = new FluidLayerSet(Registered.CONSTITUENT_WATER);
        data.replace(0.0f, new FluidLayer(Registered.CONSTITUENT_AIR, FluidLayerSet.FULL_VOLUME - amount
                * ConstituentRegistry.REGISTRY.getAttributes(Registered.CONSTITUENT_WATER).volume()));
        context.getSource().getWorld().setBlockState(blockPos, Registered.RESOLVED_FLUID_BLOCK.getDefaultState());
        chunk.setFluidData(blockPos.getX() & 15, blockPos.getY(), blockPos.getZ() & 15, data);
        return 1;
    }

}
