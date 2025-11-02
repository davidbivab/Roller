package com.david.dishonestmagic.common.capability;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class PlayerFlags implements IPlayerFlags {
    private final Map<String, Boolean> flags = new HashMap<>();
    private final Map<String, Float> values = new HashMap<>();

    @Override
    public boolean getFlag(String key) {
        return flags.getOrDefault(key, false);
    }

    @Override
    public void setFlag(String key, boolean value) {
        flags.put(key, value);
    }

    @Override
    public Map<String, Boolean> getAllFlags() {
        return flags;
    }

    @Override
    public float getValue(String key) {
        return values.getOrDefault(key, 0f);
    }

    @Override
    public void setValue(String key, float value) {
        values.put(key, value);
    }

    @Override
    public Map<String, Float> getAllValues() {
        return values;
    }

    @Override
    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();

        CompoundTag flagTag = new CompoundTag();
        for (var e : flags.entrySet()) flagTag.putBoolean(e.getKey(), e.getValue());
        tag.put("Flags", flagTag);

        CompoundTag valueTag = new CompoundTag();
        for (var e : values.entrySet()) valueTag.putFloat(e.getKey(), e.getValue());
        tag.put("Values", valueTag);

        return tag;
    }

    @Override
    public void loadNBT(CompoundTag tag) {
        flags.clear();
        values.clear();

        if (tag.contains("Flags")) {
            CompoundTag flagTag = tag.getCompound("Flags");
            for (String key : flagTag.getAllKeys()) flags.put(key, flagTag.getBoolean(key));
        }

        if (tag.contains("Values")) {
            CompoundTag valueTag = tag.getCompound("Values");
            for (String key : valueTag.getAllKeys()) values.put(key, valueTag.getFloat(key));
        }
    }
}
