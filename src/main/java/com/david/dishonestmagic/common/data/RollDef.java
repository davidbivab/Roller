package com.david.dishonestmagic.common.data;

import java.util.List;

public record RollDef(
        String id,
        List<RollFrameDef> frames
) {}
