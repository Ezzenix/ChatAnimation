package com.ezzenix.chatanimation.mixin;

import com.ezzenix.chatanimation.config.ModConfig;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiMessage.Line.class)
public class GuiMessageMixin {
	@Inject(method = "tag", at = @At("HEAD"), cancellable = true)
	private void injectIndicator(CallbackInfoReturnable<GuiMessageTag> cir) {
		if (!ModConfig.getConfig().removeMessageIndicator) return;
		cir.setReturnValue(null);
	}
}