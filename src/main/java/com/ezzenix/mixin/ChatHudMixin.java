package com.ezzenix.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
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
	@Shadow @Final private List<ChatHudLine.Visible> visibleMessages;
	@Shadow private boolean hasUnreadNewMessages;
	@Shadow @Final private MinecraftClient client;

	@Shadow private static double getMessageOpacityMultiplier(int age) { return 0; }
	@Shadow private boolean isChatHidden() { return false; }
	@Shadow private int getLineHeight() { return 0; }
	@Shadow private boolean isChatFocused() { return false; }
	@Shadow public int getVisibleLineCount() { return 0; }
	@Shadow public double getChatScale() {
		return 0;
	}
	@Shadow public int getWidth() { return 0; }

	@Unique private final ArrayList<Long> messageTimestamps = new ArrayList<>();

	@Unique private final float fadeOffsetYScale = 0.8f; // scale * lineHeight
	@Unique private final float fadeTime = 150;

	@Unique private int chatDisplacementY = 0;

	@Unique
	private void calculateYOffset() {
		// Calculate current required offset to achieve slide in from bottom effect
		try {
			int lineHeight = this.getLineHeight();
			float maxDisplacement = (float)lineHeight * fadeOffsetYScale;
			long timestamp = messageTimestamps.getFirst();
			long timeAlive = System.currentTimeMillis() - timestamp;
			if (timeAlive < fadeTime && this.scrolledLines == 0) {
				chatDisplacementY = (int)(maxDisplacement - ((timeAlive/fadeTime)*maxDisplacement));
			} else {
				chatDisplacementY = 0;
			}
		} catch (Exception ignored) {
			chatDisplacementY = 0;
		}
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void onRenderStart(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
		calculateYOffset();
		
		// Apply Raised mod compatibility
		float raisedOffset = 0;
		if (FabricLoader.getInstance().getObjectShare().get("raised:hud") instanceof Integer distance) {
			raisedOffset -= distance;
		} else if (FabricLoader.getInstance().getObjectShare().get("raised:distance") instanceof Integer distance) {
			raisedOffset -= distance;
		}
		
		context.getMatrices().translate(0, chatDisplacementY + raisedOffset);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void onRenderEnd(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
		// Apply Raised mod compatibility
		float raisedOffset = 0;
		if (FabricLoader.getInstance().getObjectShare().get("raised:hud") instanceof Integer distance) {
			raisedOffset -= distance;
		} else if (FabricLoader.getInstance().getObjectShare().get("raised:distance") instanceof Integer distance) {
			raisedOffset -= distance;
		}
		
		context.getMatrices().translate(0, -(chatDisplacementY + raisedOffset));
	}

	@Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("TAIL"))
	private void addMessage(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
		messageTimestamps.addFirst(System.currentTimeMillis());
		while (this.messageTimestamps.size() > this.visibleMessages.size()) {
			this.messageTimestamps.removeLast();
		}
	}
}