package com.david.dishonestmagic.common.event;

import com.david.dishonestmagic.Roller;
import com.david.dishonestmagic.common.capability.PlayerFlagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Roller.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerFlagsEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(Roller.MODID, "player_flags"), new PlayerFlagsProvider());
            Roller.LOGGER.debug("Attached PlayerFlagsProvider to entity: {}", event.getObject().getType().toString());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerFlagsProvider.copyForRespawn(event.getOriginal(), event.getEntity());
        Roller.LOGGER.debug("Copied PlayerFlags from old to new player after clone");
    }
}
