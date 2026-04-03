package com.ezzenix.chatanimation.mixin;

import com.ezzenix.chatanimation.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
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

	@WrapOperation(
		method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/ChatComponent;extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V"
		)
	)
	private void wrapRender(
			ChatComponent instance, ChatComponent.ChatGraphicsAccess queueMessage, int restrictedMessageWidth, int restrictedMessage, ChatComponent.DisplayMode alpha, Operation<Void> original, @Local(argsOnly = true) GuiGraphicsExtractor graphics
	) {
		float displacement = calculateDisplacement();

		if (displacement != 0) {
			graphics.pose().pushMatrix();
			graphics.pose().translate(0, displacement);
		}

		original.call(instance, queueMessage, restrictedMessageWidth, restrictedMessage, alpha);

		if (displacement != 0) {
			graphics.pose().popMatrix();
		}
	}

	@Inject(method = "addMessage", at = @At("TAIL"))
	private void addMessage(Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
		lastMessageTime = System.currentTimeMillis();
	}
}