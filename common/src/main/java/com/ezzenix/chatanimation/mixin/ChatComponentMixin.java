package com.ezzenix.chatanimation.mixin;

import com.ezzenix.chatanimation.config.ModConfig;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
	@Shadow private int chatScrollbarPos;
	@Shadow private int getLineHeight() { return 0; }

	@Unique private long lastMessageTime = 0L;

	@Unique
	private float calculateDisplacement() {
		if (!ModConfig.getConfig().enableMessageAnimation || this.chatScrollbarPos != 0) {
			return 0;
		}

		float fadeTime = (float) ModConfig.getConfig().fadeTimeMessage;

		int lineHeight = getLineHeight();
		float fadeOffsetYScale = 0.8f; // scale * lineHeight
		float maxDisplacement = (float)lineHeight * fadeOffsetYScale;
		long lifetime = System.currentTimeMillis() - lastMessageTime;
		float alpha = Math.min(lifetime/fadeTime, 1f);

		return (maxDisplacement - (alpha*maxDisplacement));
	}

	@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At("HEAD"))
	private void onRenderStart(GuiGraphics context, Font p_458144_, int p_283491_, int p_282406_, int p_283111_, boolean p_316855_, boolean p_480243_, CallbackInfo ci) {
		float displacement = calculateDisplacement();
		context.pose().pushMatrix();
		context.pose().translate(0, displacement);
	}

	@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V", at = @At("TAIL"))
	private void onRenderEnd(GuiGraphics context, Font p_458144_, int p_283491_, int p_282406_, int p_283111_, boolean p_316855_, boolean p_480243_, CallbackInfo ci) {
		context.pose().popMatrix();
	}

	@Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("TAIL"))
	private void addMessage(Component chatComponent, MessageSignature headerSignature, GuiMessageTag tag, CallbackInfo ci) {
		lastMessageTime = System.currentTimeMillis();
	}
}