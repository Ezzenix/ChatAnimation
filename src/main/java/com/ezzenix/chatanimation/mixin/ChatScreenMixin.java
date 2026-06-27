package com.ezzenix.chatanimation.mixin;

import com.ezzenix.chatanimation.ChatAnimation;
import com.ezzenix.chatanimation.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
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

    @Inject(method = "removed", at = @At("HEAD"))
    private void onClosed(CallbackInfo ci) {
        wasOpenedLastFrame = false;
    }

	//? 1.20.1 {
	/*@WrapMethod(method = "render")
	private void wrapRender(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
		ChatAnimation.wrap(guiGraphics, calculateDisplacement(), () -> original.call(guiGraphics, mouseX, mouseY, partialTick));
	}

	@WrapOperation(
		method = "render",
		at= @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"
		)
	)
	private void wrapRenderInput(ChatScreen instance, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
		ChatAnimation.wrap(guiGraphics, -calculateDisplacement(), () -> original.call(instance, guiGraphics, mouseX, mouseY, partialTick));
	}
	*///? }

    //? 1.21.1 || 1.21.10 {
    /*@WrapMethod(method = "render")
    private void wrapRender(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        ChatAnimation.wrap(guiGraphics, calculateDisplacement(), () -> original.call(guiGraphics, mouseX, mouseY, partialTick));
    }

    @WrapOperation(
            method = "render",
            at= @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIZ)V"
            )
    )
    private void wrapRenderComponent(ChatComponent instance, GuiGraphicsExtractor guiGraphics, int tickCount, int mouseX, int mouseY, boolean focused, Operation<Void> original) {
        ChatAnimation.wrap(guiGraphics, -calculateDisplacement(), () -> original.call(instance, guiGraphics, tickCount, mouseX, mouseY, focused));
    }
    *///? }

    //? 1.21.11 {
	/*@WrapOperation(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"
		)
	)
	private void wrapChatBackground(GuiGraphicsExtractor instance, int minX, int minY, int maxX, int maxY, int color, Operation<Void> original) {
		ChatAnimation.wrap(instance, calculateDisplacement(), () -> original.call(instance, minX, minY, maxX, maxY, color));
	}

	@WrapOperation(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"
		)
	)
	private void wrapScreenRender(ChatScreen instance, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
		ChatAnimation.wrap(guiGraphics, calculateDisplacement(), () -> original.call(instance, guiGraphics, mouseX, mouseY, partialTick));
	}
    *///? }

	//? 26.1 || 26.2 {
	@WrapOperation(
		method = "extractRenderState",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V")
	)
	private void wrapBackgroundFill(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int col, Operation<Void> original) {
		ChatAnimation.wrap(graphics, calculateDisplacement(), () -> original.call(graphics, x0, y0, x1, y1, col));
	}

	@WrapOperation(
		method = "extractRenderState",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V")
	)
	private void wrapSuperAndSuggestions(ChatScreen instance, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, Operation<Void> original) {
		ChatAnimation.wrap(graphics, calculateDisplacement(), () -> original.call(instance, graphics, mouseX, mouseY, delta));
	}
	//? }
}
