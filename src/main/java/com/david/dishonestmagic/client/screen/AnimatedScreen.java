package com.david.dishonestmagic.client.screen;


import com.david.dishonestmagic.common.data.RollDef;
import com.david.dishonestmagic.common.data.RollFrameDef;
import com.david.dishonestmagic.common.network.ModNetwork;
import com.david.dishonestmagic.common.network.RollFinishedPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Random;

public class AnimatedScreen extends Screen {
    private final RollDef definition;
    private final Random random = new Random();

    private RollFrameDef currentFrame;
    private long lastFrameChange;
    private long startTime;

    private boolean spinning = true;
    private boolean waitingForFinish = false;
    private boolean finishing = false;

    private final String resultFrameId;
    private final int durationMs;

    private long finishStartTime;
    private long waitStartTime;

    public AnimatedScreen(RollDef def, String resultFrameId, int durationMs) {
        super(Component.literal("Roll"));
        this.definition = def;
        this.resultFrameId = resultFrameId;
        this.durationMs = durationMs;
    }

    @Override
    protected void init() {
        this.currentFrame = getRandomFrame();
        this.lastFrameChange = System.currentTimeMillis();
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void tick() {
        long now = System.currentTimeMillis();

        if (spinning) {
            if (now - lastFrameChange > 150) {
                currentFrame = getRandomFrame();
                lastFrameChange = now;
            }

            if (now - startTime > durationMs) {
                spinning = false;
                waitingForFinish = true;
                waitStartTime = now;

                this.currentFrame = definition.frames().stream()
                        .filter(f -> f.frameId().equals(resultFrameId))
                        .findFirst()
                        .orElse(currentFrame);
            }
        } else if (waitingForFinish) {
            if (now - waitStartTime > 2000) {
                waitingForFinish = false;
                finishing = true;
                finishStartTime = now;
            }
        } else if (finishing) {
            if (now - finishStartTime > 1000) {
                ModNetwork.CHANNEL.sendToServer(new RollFinishedPacket(definition.id(), resultFrameId));
                Minecraft.getInstance().setScreen(null);
            }
        }
    }

    private RollFrameDef getRandomFrame() {
        List<RollFrameDef> frames = definition.frames();
        double totalWeight = frames.stream().mapToDouble(RollFrameDef::weight).sum();
        double roll = random.nextDouble() * totalWeight;

        for (RollFrameDef f : frames) {
            roll -= f.weight();
            if (roll <= 0) return f;
        }

        return frames.get(0);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float delta) {
        if (currentFrame == null) return;

        long now = System.currentTimeMillis();
        float elapsed = (now - lastFrameChange) / 150.0f;

        float pulse = 1.0f;
        float jumpOffset = 0f;

        if (spinning) {
            pulse = (float) (1.0 + 0.05 * Math.sin(elapsed * Math.PI * 2));
            jumpOffset = (float) Math.abs(Math.sin(elapsed * Math.PI)) * 8f;
        } else if (finishing) {
            float t = Math.min((now - finishStartTime) / 1000f, 1f);
            pulse = 1.0f + (float) Math.sin(t * Math.PI) * 0.4f;
            jumpOffset = (float) Math.sin(t * Math.PI) * 20f;
        } else if (waitingForFinish) {
            pulse = (float) (1.0 + 0.03 * Math.sin(elapsed * Math.PI));
        }

        int baseSize = 128;
        int size = (int) (baseSize * pulse);

        int x = width / 2 - size / 2;
        int y = height / 2 - size / 2 - (int) jumpOffset;

        RenderSystem.setShaderTexture(0, currentFrame.texture());
        gfx.pose().pushPose();
        gfx.blit(currentFrame.texture(), x, y, 0, 0, size, size, size, size);
        gfx.pose().popPose();

        if (finishing) {
            float t = (now - finishStartTime) / 1000f;
            if (t < 1f) {
                int alpha = (int) ((1 - t) * 150);
                int color = (alpha << 24) | 0xFFFFFF;
                gfx.fill(0, 0, width, height, color);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}