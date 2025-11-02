package com.david.dishonestmagic.common.capability;

import net.minecraft.nbt.CompoundTag;
import java.util.Map;

public interface IPlayerFlags {
    boolean getFlag(String key);
    void setFlag(String key, boolean value);
    Map<String, Boolean> getAllFlags();

    float getValue(String key);
    void setValue(String key, float value);
    Map<String, Float> getAllValues();

    CompoundTag saveNBT();
    void loadNBT(CompoundTag tag);
}