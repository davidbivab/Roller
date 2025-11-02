package com.david.dishonestmagic.common.network;

import com.david.dishonestmagic.server.logic.RollLogic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RollFinishedPacket {
    private final String rollId;
    private final String frameId;

    public RollFinishedPacket(String rollId, String frameId) {
        this.rollId = rollId;
        this.frameId = frameId;
    }

    public static void encode(RollFinishedPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.rollId);
        buf.writeUtf(pkt.frameId);
    }

    public static RollFinishedPacket decode(FriendlyByteBuf buf) {
        String rollId = buf.readUtf();
        String frameId = buf.readUtf();
        return new RollFinishedPacket(rollId, frameId);
    }

    public static void handle(RollFinishedPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                RollLogic.finishRoll(player, pkt.rollId, pkt.frameId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}