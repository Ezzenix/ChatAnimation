package com.ezzenix.chatanimation.mixin;

import com.ezzenix.chatanimation.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Unique private boolean wasOpenedLastFrame = false;
    @Unique private long lastOpenTime = 0;
    @Unique private float displacement = 0;

    @Unique
    private float calculateDisplacement() {
        if (!ModConfig.getConfig().enableTextFieldAnimation) {
            return 0;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.player != null && !wasOpenedLastFrame && !client.player.isSleeping()) {
            wasOpenedLastFrame = true;
            lastOpenTime = System.currentTimeMillis();
        }

        float FADE_TIME = (float) ModConfig.getConfig().fadeTimeTextField;
        float FADE_OFFSET = 8;
        float screenFactor = (float)client.getWindow().getHeight() / 1080;
        float timeSinceOpen = Math.min((float)(System.currentTimeMillis() - lastOpenTime), FADE_TIME);
        float alpha = 1 - (timeSinceOpen/FADE_TIME);

        float c1 = 1.70158f;
        float c3 = c1 + 1;
        float modifiedAlpha = c3 * alpha * alpha * alpha - c1 * alpha * alpha;

        return modifiedAlpha * FADE_OFFSET * screenFactor;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void renderStart(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        displacement = calculateDisplacement();

        if (displacement != 0) {
            context.pose().pushMatrix();
            context.pose().translate(0, displacement);
        }
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V",
            shift = At.Shift.AFTER
        )
    )
    private void renderEnd(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (displacement != 0) {
            context.pose().popMatrix();
        }
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            shift = At.Shift.BEFORE
        )
    )
    private void renderScreenStart(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (displacement != 0) {
            context.pose().pushMatrix();
            context.pose().translate(0, displacement);
        }
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            shift = At.Shift.AFTER
        )
    )
    private void renderScreenEnd(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (displacement != 0) {
            context.pose().popMatrix();
        }
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void onClosed(CallbackInfo ci) {
        wasOpenedLastFrame = false;
    }
}