package com.david.dishonestmagic.common.data;

import com.david.dishonestmagic.Roller;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

public class RollManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final Map<String, RollDef> ROLLS = new HashMap<>();
    public static final RollManager INSTANCE = new RollManager();

    private RollManager() {
        super(GSON, "rolls");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        ROLLS.clear();

        for (Map.Entry<ResourceLocation, JsonElement> e : jsonMap.entrySet()) {
            try {
                JsonObject obj = e.getValue().getAsJsonObject();
                String id = obj.get("id").getAsString();
                JsonArray arr = obj.getAsJsonArray("frames");

                List<RollFrameDef> frames = new ArrayList<>();
                for (JsonElement fe : arr) {
                    JsonObject fo = fe.getAsJsonObject();
                    String frameId = fo.get("frame_id").getAsString();
                    ResourceLocation tex = new ResourceLocation(fo.get("texture").getAsString());
                    double weight = fo.get("weight").getAsDouble();
                    int duration = fo.has("duration") ? fo.get("duration").getAsInt() : 20;

                    List<ActionDef> actions = new ArrayList<>();
                    if (fo.has("actions")) {
                        JsonArray acts = fo.getAsJsonArray("actions");
                        for (JsonElement ae : acts) {
                            actions.add(GSON.fromJson(ae, ActionDef.class));
                        }
                    }

                    frames.add(new RollFrameDef(frameId, tex, weight, duration, actions));
                }

                RollDef def = new RollDef(id, frames);
                ROLLS.put(id, def);
            } catch (Exception ex) {
                Roller.LOGGER.error("Failed to parse roll json {} : {}", e.getKey(), ex);
            }
        }

        Roller.LOGGER.info("RollManager: loaded {} rolls", ROLLS.size());
    }

    public static Optional<RollDef> get(String id) {
        return Optional.ofNullable(ROLLS.get(id));
    }

    public static Collection<RollDef> all() {
        return ROLLS.values();
    }
}