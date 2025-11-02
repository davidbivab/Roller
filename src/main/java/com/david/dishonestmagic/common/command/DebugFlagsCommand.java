package com.david.dishonestmagic.common.command;


import com.david.dishonestmagic.Roller;
import com.david.dishonestmagic.common.capability.PlayerFlagsProvider;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DebugFlagsCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        Roller.LOGGER.info("Registering /debug_flags command");
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("debug_flags")
                .requires(src -> src.hasPermission(2))
                .executes(ctx -> {
                    Roller.LOGGER.info("/debug_flags executed");
                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                    player.getCapability(PlayerFlagsProvider.PLAYER_FLAGS).ifPresent(cap -> {
                        String flagsStr = cap.getAllFlags().toString();
                        String valuesStr = cap.getAllValues().toString();

                        Roller.LOGGER.info("Player {} flags: {}", player.getName().getString(), flagsStr);
                        Roller.LOGGER.info("Player {} values: {}", player.getName().getString(), valuesStr);

                        ctx.getSource().sendSuccess(() -> Component.literal("Flags: " + flagsStr), false);
                        ctx.getSource().sendSuccess(() -> Component.literal("Values: " + valuesStr), false);
                    });

                    if (!player.getCapability(PlayerFlagsProvider.PLAYER_FLAGS).isPresent()) {
                        Roller.LOGGER.warn("Player {} has no PlayerFlags capability attached", player.getName().getString());
                        ctx.getSource().sendFailure(Component.literal("No player flags capability found for you"));
                    }

                    return 1;
                }));
    }
}