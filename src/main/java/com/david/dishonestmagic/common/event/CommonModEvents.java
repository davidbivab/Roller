package com.david.dishonestmagic.common.event;

import com.david.dishonestmagic.Roller;
import com.david.dishonestmagic.common.data.RollManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Roller.MODID)
public class CommonModEvents {
    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(RollManager.INSTANCE);
    }
}
