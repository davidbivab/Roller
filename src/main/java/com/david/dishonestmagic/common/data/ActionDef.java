package com.david.dishonestmagic.common.data;


public record ActionDef(
        String type,
        String item,
        String effect,
        String attribute,
        String operation,
        double modifier,
        int duration,
        int amplifier,
        int count,
        String key,
        boolean flagValue,
        float value
) {}