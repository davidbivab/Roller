package com.david.dishonestmagic.common.network;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

public class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("dishonestmagic", "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, StartRollPacket.class,
                StartRollPacket::encode, StartRollPacket::decode, StartRollPacket::handle);
        CHANNEL.registerMessage(id++, RollFinishedPacket.class,
                RollFinishedPacket::encode, RollFinishedPacket::decode, RollFinishedPacket::handle);
    }
}
