package com.ezzenix.chatanimation.mixin;

import com.ezzenix.chatanimation.ChatAnimation;
import com.ezzenix.chatanimation.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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
        float fadeOffsetYScale = 0.8f;
        float maxDisplacement = (float)lineHeight * fadeOffsetYScale;
        long lifetime = System.currentTimeMillis() - lastMessageTime;
        float alpha = Math.min(lifetime/fadeTime, 1f);

        return (maxDisplacement - (alpha*maxDisplacement));
    }

	/*
		STORE WHEN LAST MESSAGE WAS ADDED
	 */

	//? >=26.1 {
	@Inject(method = "addMessage", at = @At("TAIL"))
	private void addMessage(Component contents, MessageSignature signature, net.minecraft.client.multiplayer.chat.GuiMessageSource source, net.minecraft.client.multiplayer.chat.GuiMessageTag tag, CallbackInfo ci) {
	//? } else {
	/*@Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("TAIL"))
	private void addMessage(Component chatComponent, MessageSignature headerSignature, net.minecraft.client.multiplayer.chat.GuiMessageTag tag, CallbackInfo ci) {
	*///? }
		lastMessageTime = System.currentTimeMillis();
	}

	/*
		CHAT OFFSET
	 */

	//? <=1.20.4 {
	/*@WrapMethod(method = "render")
	private void wrapRender(GuiGraphicsExtractor guiGraphics, int tickCount, int mouseX, int mouseY, Operation<Void> original) {
		ChatAnimation.wrap(guiGraphics, calculateDisplacement(), () -> original.call(guiGraphics, tickCount, mouseX, mouseY));
	}
	*///? }

    //? >=1.20.5 && <=1.21.10 {
    /*@WrapMethod(method = "render")
	private void wrapRender(GuiGraphicsExtractor guiGraphics, int tickCount, int mouseX, int mouseY, boolean focused, Operation<Void> original) {
		ChatAnimation.wrap(guiGraphics, calculateDisplacement(), () -> original.call(guiGraphics, tickCount, mouseX, mouseY, focused));
	}
    *///? }

    //? 1.21.11 {
	/*@WrapOperation(
		method = "render(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIIZZ)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IIZ)V"
		)
	)
	private void wrapRender(ChatComponent instance, ChatComponent.ChatGraphicsAccess guiGraphicsAccess, int i, int j, boolean bl, Operation<Void> original, @Local(argsOnly = true) GuiGraphicsExtractor context) {
		ChatAnimation.wrap(context, calculateDisplacement(), () -> original.call(instance, guiGraphicsAccess, i, j, bl));
	}
    *///? }

	//? >=26.1 {
	@WrapOperation(
		method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/ChatComponent;extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V"
		)
	)
	private void wrapRender(ChatComponent instance, ChatComponent.ChatGraphicsAccess queueMessage, int restrictedMessageWidth, int restrictedMessage, ChatComponent.DisplayMode alpha, Operation<Void> original, @Local(argsOnly = true) GuiGraphicsExtractor graphics) {
		ChatAnimation.wrap(graphics, calculateDisplacement(), () -> original.call(instance, queueMessage, restrictedMessageWidth, restrictedMessage, alpha));
	}
	//? }

	/*
		OPACITY FADING
	 */

	//? <=1.21.5 {
	/*@ModifyVariable(method = "render", at = @At("STORE"), ordinal = 3)
	private double modifyChatOpacity(double original, @Local GuiMessage.Line line, @Local(argsOnly = true, ordinal = 0) int currentTick) {
		return original * ChatAnimation.getOpacityFactor(currentTick - line.addedTime());
	}
	*///? }

	//? >=1.21.6 {
	@ModifyVariable(method = "forEachLine", at = @At("STORE"), ordinal = 0)
	private float modifyChatOpacity(float original, @Local GuiMessage.Line line) {
		int currentTick = Minecraft.getInstance().gui.hud.getGuiTicks();
		return original * (float)ChatAnimation.getOpacityFactor(currentTick - line.addedTime());
	}
	//? }

}
