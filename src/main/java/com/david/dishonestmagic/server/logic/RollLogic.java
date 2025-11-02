package com.david.dishonestmagic.server.logic;

import com.david.dishonestmagic.Roller;
import com.david.dishonestmagic.common.data.RollDef;
import com.david.dishonestmagic.common.data.RollFrameDef;
import com.david.dishonestmagic.common.data.RollManager;
import com.david.dishonestmagic.common.network.ModNetwork;
import com.david.dishonestmagic.common.network.StartRollPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class RollLogic {
    private static final Map<UUID, PendingRoll> pendingRolls = new HashMap<>();

    public static void startRoll(ServerPlayer player, String rollId) {
        Optional<RollDef> rollOpt = RollManager.get(rollId);
        if (rollOpt.isEmpty()) {
            Roller.LOGGER.error("Roll '{}' not found", rollId);
            return;
        }

        RollDef roll = rollOpt.get();

        RollFrameDef result = chooseFrame(roll);

        pendingRolls.put(player.getUUID(), new PendingRoll(roll.id(), result.frameId()));

        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new StartRollPacket(rollId,result.frameId(),5000));
    }

    public static void finishRoll(ServerPlayer player, String rollId, String frameId) {
        PendingRoll pending = pendingRolls.remove(player.getUUID());
        if (pending == null || !pending.rollId.equals(rollId)) {
            Roller.LOGGER.warn("Player {} finished unknown roll {}", player.getName().getString(), rollId);
            return;
        }

        if (!pending.frameId.equals(frameId)) {
            Roller.LOGGER.warn("Player {} reported unexpected frame '{}' (expected '{}')",
                    player.getName().getString(), frameId, pending.frameId);
            return;
        }

        applyResult(player, pending.rollId, pending.frameId);
    }

    public static RollFrameDef chooseFrame(RollDef roll) {
        double totalWeight = roll.frames().stream().mapToDouble(RollFrameDef::weight).sum();
        double rnd = new Random().nextDouble() * totalWeight;
        for (RollFrameDef f : roll.frames()) {
            rnd -= f.weight();
            if (rnd <= 0) return f;
        }
        return roll.frames().get(0);
    }

    public static void applyResult(ServerPlayer player, String rollId, String frameId) {
        Optional<RollDef> rollOpt = RollManager.get(rollId);
        if (rollOpt.isEmpty()) return;

        RollDef roll = rollOpt.get();
        RollFrameDef frame = roll.frames().stream()
                .filter(f -> f.frameId().equals(frameId))
                .findFirst()
                .orElse(null);

        if (frame == null) {
            Roller.LOGGER.error("Frame '{}' not found in roll '{}'", frameId, rollId);
            return;
        }

        frame.actions().forEach(a -> ActionLogic.applyAction(player, a));

        Roller.LOGGER.info("{} получил результат '{}' из рулетки '{}'", player.getName().getString(), frameId, rollId);
    }

    private record PendingRoll(String rollId, String frameId) {}
}
