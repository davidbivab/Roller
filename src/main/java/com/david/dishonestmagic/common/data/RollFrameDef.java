package com.david.dishonestmagic.common.data;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record RollFrameDef(
        String frameId,
        ResourceLocation texture,
        double weight,
        int duration,
        List<ActionDef> actions
) {}
