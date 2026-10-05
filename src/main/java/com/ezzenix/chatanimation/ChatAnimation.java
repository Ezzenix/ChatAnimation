package com.ezzenix.chatanimation;

import com.ezzenix.chatanimation.config.ModConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.ezzenix.emlib.config.EmConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? if forge {
/*import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(value = ChatAnimation.MOD_ID)
public class ChatAnimation {
*///? }

//? if neoforge {
/*import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.api.distmarker.Dist;

@Mod(value = ChatAnimation.MOD_ID, dist = Dist.CLIENT)
public class ChatAnimation {
*///? }

//? if fabric {
import net.fabricmc.api.ModInitializer;

public class ChatAnimation implements ModInitializer {
//? }

    public static final String MOD_ID = "chatanimation";
    public static final String MOD_NAME = "ChatAnimation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	private static void initialize() {
		EmConfig.init(MOD_ID, ModConfig.class);
	}

	public static void wrap(GuiGraphicsExtractor graphics, float displacement, Runnable runnable) {
		if (displacement != 0) {
			//~ if >=1.21.6 'pushPose' -> 'pushMatrix'
			graphics.pose().pushMatrix();
			//~ if >=1.21.6 '(0, displacement, 0)' -> '(0, displacement)'
			graphics.pose().translate(0, displacement);
		}
		runnable.run();
		if (displacement != 0) {
			//~ if >=1.21.6 'popPose' -> 'popMatrix'
			graphics.pose().popMatrix();
		}
	}

	public static double getOpacityFactor(float age) {
		if (!ModConfig.enableMessageAnimation || !ModConfig.enableOpacity) {
			return 1;
		}
		float fadeTime = (float) ModConfig.fadeTimeMessage;
		if (fadeTime <= 0) return 1;
		return Math.min(age / fadeTime, 1.0F);
	}

	//? if forge {
	/*public ChatAnimation() {
		initialize();
	}
    *///? }

	/*? if neoforge {*/
    /*public ChatAnimation(ModContainer container) {
       initialize();
    }
    *//*?}*/

	/*? if fabric {*/
	@Override
	public void onInitialize() {
		initialize();
	}
	/*?}*/
}
