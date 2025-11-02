package com.david.dishonestmagic.server.logic;

import com.david.dishonestmagic.Roller;
import com.david.dishonestmagic.common.capability.PlayerFlagsProvider;
import com.david.dishonestmagic.common.data.ActionDef;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class ActionLogic {

    public static void applyAction(ServerPlayer player, ActionDef action) {
        switch (action.type()) {
            case "give_item" -> giveItem(player, action);
            case "effect" -> giveEffect(player, action);
            case "attribute" -> modifyAttribute(player, action);
            case "set_flag" -> setFlag(player, action);
            case "set_value" -> setValue(player, action);
            case "add_value" -> addValue(player, action);
            default -> Roller.LOGGER.warn("Unknown action type: {}", action.type());
        }
    }

    // --------------------------------------------------------
    //  GIVE ITEM
    // --------------------------------------------------------
    private static void giveItem(ServerPlayer player, ActionDef action) {
        var item = BuiltInRegistries.ITEM.get(new ResourceLocation(action.item()));
        if (item == null) {
            Roller.LOGGER.warn("Unknown item: {}", action.item());
            return;
        }
        player.addItem(new ItemStack(item, Math.max(1, action.count())));
    }

    // --------------------------------------------------------
    //  EFFECT
    // --------------------------------------------------------
    private static void giveEffect(ServerPlayer player, ActionDef action) {
        var effect = BuiltInRegistries.MOB_EFFECT.get(new ResourceLocation(action.effect()));
        if (effect == null) {
            Roller.LOGGER.warn("Unknown effect: {}", action.effect());
            return;
        }
        player.addEffect(new MobEffectInstance(effect, action.duration(), action.amplifier()));
    }

    // --------------------------------------------------------
    //  ATTRIBUTE
    // --------------------------------------------------------
    private static void modifyAttribute(ServerPlayer player, ActionDef action) {
        var attr = BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(action.attribute()));
        if (attr == null) {
            Roller.LOGGER.warn("Unknown attribute: {}", action.attribute());
            return;
        }

        var instance = player.getAttribute(attr);
        if (instance == null) {
            Roller.LOGGER.warn("Player has no attribute: {}", attr);
            return;
        }

        AttributeModifier.Operation op = switch (action.operation()) {
            case "multiply_base" -> AttributeModifier.Operation.MULTIPLY_BASE;
            case "multiply_total" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
            default -> AttributeModifier.Operation.ADDITION;
        };

        UUID id = UUID.nameUUIDFromBytes(("roll_action_" + action.attribute()).getBytes());
        AttributeModifier mod = new AttributeModifier(id, "Roll action modifier", action.modifier(), op);

        if (instance.getModifier(id) != null)
            instance.removeModifier(id);

        instance.addPermanentModifier(mod);
    }

    // --------------------------------------------------------
    //  PLAYER FLAG SYSTEM
    // --------------------------------------------------------

    private static void setFlag(ServerPlayer player, ActionDef action) {
        String key = action.key();
        boolean value = action.flagValue();
        player.getCapability(PlayerFlagsProvider.PLAYER_FLAGS).ifPresent(flags -> {
            flags.setFlag(key, value);
        });
        Roller.LOGGER.debug("Set flag {} = {} for {}", key, value, player.getName().getString());
    }

    private static void setValue(ServerPlayer player, ActionDef action) {
        String key = action.key();
        float value = action.value();
        player.getCapability(PlayerFlagsProvider.PLAYER_FLAGS).ifPresent(flags -> {
            flags.setValue(key, value);
        });
        Roller.LOGGER.debug("Set value {} = {} for {}", key, value, player.getName().getString());
    }

    private static void addValue(ServerPlayer player, ActionDef action) {
        String key = action.key();
        float delta = action.value();
        player.getCapability(PlayerFlagsProvider.PLAYER_FLAGS).ifPresent(flags -> {
            float current = flags.getValue(key);
            flags.setValue(key, current + delta);
        });
        Roller.LOGGER.debug("Add value {} += {} for {}", key, delta, player.getName().getString());
    }
}