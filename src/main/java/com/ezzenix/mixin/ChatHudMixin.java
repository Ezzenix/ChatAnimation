package com.ezzenix.mixin;

import com.ezzenix.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ChatHud.class)
public class ChatHudMixin {
	@Shadow private int scrolledLines;
	@Shadow private int getLineHeight() { return 0; }

	@Unique private long lastMessageTime = 0L;

	@Unique
	private float calculateDisplacement() {
		if (!ModConfig.getConfig().enableMessageAnimation || this.scrolledLines != 0) {
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

	@Inject(method = "render", at = @At("HEAD"))
	private void onRenderStart(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
		float displacement = calculateDisplacement();
		context.getMatrices().pushMatrix();
		context.getMatrices().translate(0, displacement);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void onRenderEnd(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
		context.getMatrices().popMatrix();
	}

	@Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("TAIL"))
	private void addMessage(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
		lastMessageTime = System.currentTimeMillis();
	}
}