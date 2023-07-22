package ezzenix.chatanimation.mixin;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ChatHud.class)
public class ChatMixin {
	@Shadow private int scrolledLines;
	//@Shadow @Final private List<ChatHudLine> messages;
	@Shadow @Final private List<ChatHudLine.Visible> visibleMessages;
	@Shadow private boolean hasUnreadNewMessages;
	@Shadow @Final private MinecraftClient client;
	//@Shadow @Final private static final Logger LOGGER = LogUtils.getLogger();

	//@Shadow private void drawIndicatorIcon(DrawContext context, int x, int y, MessageIndicator.Icon icon) { }
	//@Shadow private int getIndicatorX(ChatHudLine.Visible line) { return 0; }
	@Shadow private static double getMessageOpacityMultiplier(int age) { return 0; }
	@Shadow private boolean isChatHidden() { return false; }
	@Shadow private int getLineHeight() { return 0; }
	@Shadow private boolean isChatFocused() { return false; }
	@Shadow private double toChatLineX(double x) { return 0; }
	@Shadow private double toChatLineY(double y) { return 0; }
	@Shadow private int getMessageIndex(double chatLineX, double chatLineY) { return 0; }
	@Shadow public int getVisibleLineCount() { return 0; }
	@Shadow public double getChatScale() {
		return 0;
	}
	@Shadow public int getWidth() { return 0; }

	@Unique private final ArrayList<Long> messageTimestamps = new ArrayList<>();

	@Unique private final float fadeOffsetYScale = 0.8f; // scale * lineHeight
	@Unique private final float fadeTime = 130;

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(DrawContext context, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
		int y;
		int w;
		int textBackgroundOpacityFinal;
		int chatOpacityFinal;
		int ticksAlive;

		if (this.isChatHidden()) {
			return;
		}

		int visibleLineCount = this.getVisibleLineCount();
		int visibleMessagesCount = this.visibleMessages.size();
		if (visibleMessagesCount == 0) {
			return;
		}

		boolean isFocused = this.isChatFocused();
		float chatScale = (float)this.getChatScale();
		int k = MathHelper.ceil((float)this.getWidth() / chatScale);
		int scaledWindowHeight = context.getScaledWindowHeight();

		context.getMatrices().push();
		context.getMatrices().scale(chatScale, chatScale, 1.0f);
		context.getMatrices().translate(4.0f, 0.0f, 0.0f);

		int chatBottomY = MathHelper.floor((float)(scaledWindowHeight - 40) / chatScale);
		//int n = this.getMessageIndex(this.toChatLineX(mouseX), this.toChatLineY(mouseY));
		double chatOpacity = this.client.options.getChatOpacity().getValue() * (double)0.9f + (double)0.1f;
		double textBackgroundOpacity = this.client.options.getTextBackgroundOpacity().getValue();
		double chatLineSpacing = this.client.options.getChatLineSpacing().getValue();
		int lineHeight = this.getLineHeight();
		int lineSpacing = (int)Math.round(-8.0 * (chatLineSpacing + 1.0) + 4.0 * chatLineSpacing);
		int index = 0;
		int chatDisplacementY = 0;
		float maxDisplacement = (float)lineHeight * fadeOffsetYScale;

		for (int r = 0; r + this.scrolledLines < this.visibleMessages.size() && r < visibleLineCount; ++r) {
			int s = r + this.scrolledLines;
			ChatHudLine.Visible visible = this.visibleMessages.get(s);
			if (visible == null || (ticksAlive = currentTick - visible.addedTime()) >= 200 && !isFocused) continue;

			long timestamp = messageTimestamps.get(r);
			long timeAlive = System.currentTimeMillis() - timestamp;
			if (r == 0 && timeAlive < fadeTime && this.scrolledLines == 0) {
				chatDisplacementY = (int)(maxDisplacement - ((timeAlive/fadeTime)*maxDisplacement));
			}

			double opacity = isFocused ? 1.0 : getMessageOpacityMultiplier(ticksAlive);
			if (timeAlive < fadeTime && this.scrolledLines == 0) {
				opacity = opacity * (0.5 + MathHelper.clamp(timeAlive/fadeTime, 0, 1)/2);
			}

			chatOpacityFinal = (int)(255.0 * opacity * chatOpacity);
			textBackgroundOpacityFinal = (int)(255.0 * opacity * textBackgroundOpacity);
			++index;

			if (chatOpacityFinal <= 3) continue;

			y = chatBottomY - r * lineHeight + chatDisplacementY;
			context.getMatrices().push();
			context.getMatrices().translate(0.0f, 0.0f, 50.0f);
			context.fill(-4, y - lineHeight, k + 4 + 4, y, textBackgroundOpacityFinal << 24);
			/*
			MessageIndicator messageIndicator = visible.indicator();
			if (messageIndicator != null) {
				int z = messageIndicator.indicatorColor() | u << 24;
				context.fill(-4, x - lineHeight, -2, x, z);
				if (s == n && messageIndicator.icon() != null) {
					int aa = this.getIndicatorX(visible);
					int ab = y + this.client.textRenderer.fontHeight;
					this.drawIndicatorIcon(context, aa, ab, messageIndicator.icon());
				}
			}
			*/
			context.getMatrices().translate(0.0f, 0.0f, 50.0f);
			context.drawTextWithShadow(this.client.textRenderer, visible.content(), 0, y + lineSpacing, 0xFFFFFF + (chatOpacityFinal << 24));
			context.getMatrices().pop();
		}
		long ac = this.client.getMessageHandler().getUnprocessedMessageCount();
		if (ac > 0L) {
			int ad = (int)(128.0 * chatOpacity);
			ticksAlive = (int)(255.0 * textBackgroundOpacity);
			context.getMatrices().push();
			context.getMatrices().translate(0.0f, chatBottomY, 50.0f);
			context.fill(-2, 0, k + 4, 9, ticksAlive << 24);
			context.getMatrices().translate(0.0f, 0.0f, 50.0f);
			context.drawTextWithShadow(this.client.textRenderer, Text.translatable("chat.queue", ac), 0, 1, 0xFFFFFF + (ad << 24));
			context.getMatrices().pop();
		}
		if (isFocused) {
			int ad = this.getLineHeight();
			ticksAlive = visibleMessagesCount * ad;
			int ae = index * ad;
			int af = this.scrolledLines * ae / visibleMessagesCount - chatBottomY;
			chatOpacityFinal = ae * ae / ticksAlive;
			if (ticksAlive != ae) {
				textBackgroundOpacityFinal = af > 0 ? 170 : 96;
				w = this.hasUnreadNewMessages ? 0xCC3333 : 0x3333AA;
				y = k + 4;
				context.fill(y, -af, y + 2, -af - chatOpacityFinal, w + (textBackgroundOpacityFinal << 24));
				context.fill(y + 2, -af, y + 1, -af - chatOpacityFinal, 0xCCCCCC + (textBackgroundOpacityFinal << 24));
			}
		}
		context.getMatrices().pop();

		ci.cancel();
	}

	@Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At("TAIL"))
	private void addMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
		messageTimestamps.add(0, System.currentTimeMillis());
		while (this.messageTimestamps.size() > this.visibleMessages.size()) {
			this.messageTimestamps.remove(this.messageTimestamps.size() - 1);
		}
	}
}