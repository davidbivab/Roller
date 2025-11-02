package com.david.dishonestmagic.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerFlagsProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<IPlayerFlags> PLAYER_FLAGS =
            CapabilityManager.get(new CapabilityToken<>() {});

    private final IPlayerFlags backend = new PlayerFlags();
    private final LazyOptional<IPlayerFlags> optional = LazyOptional.of(() -> backend);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == PLAYER_FLAGS ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return backend.saveNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        backend.loadNBT(nbt);
    }

    public static void copyForRespawn(Player oldPlayer, Player newPlayer) {
        oldPlayer.reviveCaps();
        oldPlayer.getCapability(PLAYER_FLAGS).ifPresent(oldCap -> {
            newPlayer.getCapability(PLAYER_FLAGS).ifPresent(newCap -> {
                newCap.loadNBT(oldCap.saveNBT());
            });
        });
        oldPlayer.invalidateCaps();
    }
}