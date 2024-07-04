package com.ezzenix.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Unique private boolean wasOpenedLastFrame = false;
    @Unique private long lastOpenTime = 0;
    @Unique private float offsetY = 0;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;IIIZ)V", shift = At.Shift.AFTER))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            if (!wasOpenedLastFrame && !client.player.isSleeping()) {
                wasOpenedLastFrame = true;
                lastOpenTime = System.currentTimeMillis();
            }
        }

        float FADE_TIME = 170;
        float FADE_OFFSET = 8;
        float screenFactor = (float)client.getWindow().getHeight() / 1080;
        float timeSinceOpen = Math.min((float)(System.currentTimeMillis() - lastOpenTime), FADE_TIME);
        float alpha = 1 - (timeSinceOpen/FADE_TIME);

        float c1 = 1.70158f;
        float c3 = c1 + 1;
        float modifiedAlpha = c3 * alpha * alpha * alpha - c1 * alpha * alpha;

        offsetY = modifiedAlpha * FADE_OFFSET * screenFactor;

        context.getMatrices().translate(0, offsetY, 0);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderEnd(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        context.getMatrices().translate(0, -offsetY, 0);
    }
}
