package com.ezzenix.chatanimation.mixin;

import com.ezzenix.chatanimation.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.GuiMessageTag;
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

	@WrapOperation(
		method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V"
		)
	)
	private void wrapRender(
		ChatComponent instance, ChatComponent.ChatGraphicsAccess guiGraphicsAccess, int i, int j, boolean bl, Operation<Void> original,
		@Local(argsOnly = true) GuiGraphics context
	) {
		float displacement = calculateDisplacement();

		if (displacement != 0) {
			context.pose().pushMatrix();
			context.pose().translate(0, displacement);
		}

		original.call(instance, guiGraphicsAccess, i, j, bl);

		if (displacement != 0) {
			context.pose().popMatrix();
		}
	}

	@Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("TAIL"))
	private void addMessage(Component chatComponent, MessageSignature headerSignature, GuiMessageTag tag, CallbackInfo ci) {
		lastMessageTime = System.currentTimeMillis();
	}
}