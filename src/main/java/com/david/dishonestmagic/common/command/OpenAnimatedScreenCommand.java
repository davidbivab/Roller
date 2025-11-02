package com.david.dishonestmagic.common.command;

import com.david.dishonestmagic.common.data.*;
import com.david.dishonestmagic.common.network.ModNetwork;
import com.david.dishonestmagic.common.network.StartRollPacket;
import com.david.dishonestmagic.server.logic.RollLogic;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OpenAnimatedScreenCommand {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("roll").requires(src -> src.hasPermission(2))
                .then(Commands.argument("id", StringArgumentType.string())
                        .executes(ctx -> {
                            String rollId = StringArgumentType.getString(ctx, "id");
                            ServerPlayer player = ctx.getSource().getPlayer();

                            Optional<RollDef> def = RollManager.get(rollId);
                            if (def.isEmpty()) {
                                ctx.getSource().sendFailure(net.minecraft.network.chat.Component.literal("No such roll: " + rollId));
                                return 0;
                            }

                            RollLogic.startRoll(player,rollId);
                            return 1;
                        })));
    }
}
