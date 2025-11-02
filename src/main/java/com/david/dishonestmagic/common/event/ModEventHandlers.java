package com.david.dishonestmagic.common.event;

import com.david.dishonestmagic.Roller;
import com.david.dishonestmagic.common.capability.IPlayerFlags;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Roller.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandlers {
    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(IPlayerFlags.class);
    }
}