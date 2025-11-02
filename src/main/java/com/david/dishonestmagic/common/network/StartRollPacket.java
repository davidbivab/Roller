package com.david.dishonestmagic.common.network;

import com.david.dishonestmagic.client.screen.AnimatedScreen;
import com.david.dishonestmagic.common.data.RollDef;
import com.david.dishonestmagic.common.data.RollManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class StartRollPacket {
    private final String rollId;
    private final String resultFrameId;
    private final int durationMs;

    public StartRollPacket(String rollId, String resultFrameId, int durationMs) {
        this.rollId = rollId;
        this.resultFrameId = resultFrameId;
        this.durationMs = durationMs;
    }

    public static void encode(StartRollPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.rollId);
        buf.writeUtf(pkt.resultFrameId);
        buf.writeInt(pkt.durationMs);
    }

    public static StartRollPacket decode(FriendlyByteBuf buf) {
        return new StartRollPacket(buf.readUtf(), buf.readUtf(), buf.readInt());
    }

    public static void handle(StartRollPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Optional<RollDef> def = RollManager.get(pkt.rollId);

            if (def.isPresent()) {
                mc.setScreen(new AnimatedScreen(def.orElse(null), pkt.resultFrameId, pkt.durationMs));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
