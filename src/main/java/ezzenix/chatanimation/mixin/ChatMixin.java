package ezzenix.chatanimation.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ChatHud.class)
public class ChatMixin {
    @Shadow private int scrolledLines;
    @Shadow @Final private List<ChatHudLine.Visible> visibleMessages;
    @Shadow private boolean hasUnreadNewMessages;
    @Shadow @Final private MinecraftClient client;

    @Shadow private static double getMessageOpacityMultiplier(int age) { return 0; }
    @Shadow private boolean isChatHidden() { return false; }
    @Shadow private int getLineHeight() { return 0; }
    @Shadow private boolean isChatFocused() { return false; }
    @Shadow public int getVisibleLineCount() { return 0; }
    @Shadow public double getChatScale() { return 0; }
    @Shadow public int getWidth() { return 0; }

    @Unique private final ArrayList<Long> messageTimestamps = new ArrayList<>();

    @Unique private final float fadeOffsetYScale = 0.8f; // scale * lineHeight
    @Unique private final float fadeTime = 130;

    private int chatLineIndex;
    private int chatDisplacementY = 0;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;addedTime()I"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void getChatLineIndex(MatrixStack matrices, int currentTick, CallbackInfo ci, int i, int j, boolean bl, float f, int k, double d, double e, double g, int l, double h, int m, int n, ChatHudLine.Visible visible) {
        // Capture which chat line is currently being rendered
        this.chatLineIndex = m;
    }

    private void calculateYOffset() {
        // Calculate current required offset to achieve slide in from bottom effect
        try {
            int lineHeight = this.getLineHeight();
            float maxDisplacement = (float)lineHeight * fadeOffsetYScale;
            long timestamp = messageTimestamps.get(chatLineIndex);
            long timeAlive = System.currentTimeMillis() - timestamp;
            if (chatLineIndex == 0 && timeAlive < fadeTime && this.scrolledLines == 0) {
                chatDisplacementY = (int)(maxDisplacement - ((timeAlive/fadeTime)*maxDisplacement));
            }
        } catch (Exception ignored) {}
    }

    @ModifyArg(method = "render", index = 1, at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V",
        ordinal = 1
    ))
    private double applyYOffset(double y) {
        // Apply the offset
        calculateYOffset();

        // Raised mod compatibility
        if (FabricLoader.getInstance().getObjectShare().get("raised:hud") instanceof Integer distance) {
            // for Raised 1.2.0+
            y -= distance;
        } else if (FabricLoader.getInstance().getObjectShare().get("raised:distance") instanceof Integer distance) {
            y -= distance;
        }

        return y + chatDisplacementY;
    }

    @ModifyVariable(method = "render", ordinal = 4, at = @At(
        value = "STORE", ordinal = 0
    ))
    private double modifyOpacity(double originalOpacity) {
        double opacity = originalOpacity;
        // Calculate current required opacity for currently rendered line to achieve fade in effect
        try {
            long timestamp = messageTimestamps.get(chatLineIndex);
            long timeAlive = System.currentTimeMillis() - timestamp;
            if (timeAlive < fadeTime && this.scrolledLines == 0) {
                opacity = opacity * (0.5 + MathHelper.clamp(timeAlive/fadeTime, 0, 1)/2);
            }
        } catch (Exception ignored) {}
        return opacity;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;indicator()Lnet/minecraft/client/gui/hud/MessageIndicator;"
    ))
    private MessageIndicator removeMessageIndicator(ChatHudLine.Visible instance) {
        // Don't allow the chat indicator bar to be rendered
        return null;
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At("TAIL"))
    private void addMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        messageTimestamps.add(0, System.currentTimeMillis());
        while (this.messageTimestamps.size() > this.visibleMessages.size()) {
            this.messageTimestamps.remove(this.messageTimestamps.size() - 1);
        }
    }
}