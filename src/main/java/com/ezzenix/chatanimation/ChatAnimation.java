package com.ezzenix.chatanimation;

import java.io.File;

import com.ezzenix.chatanimation.config.ModConfig;
import com.ezzenix.chatanimation.config.ModConfigScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*? if fabric {*/
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
/*?}*/

/*? if forge {*/
/*import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.loading.FMLPaths;
*//*?}*/

/*? if neoforge {*/
/*import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
*//*?}*/

/*? if forge {*/
/*@Mod(value = ChatAnimation.MOD_ID)
public class ChatAnimation {
*//*?}*/

/*? if neoforge {*/
/*@Mod(value = ChatAnimation.MOD_ID, dist = Dist.CLIENT)
public class ChatAnimation {
*//*?}*/

/*? if fabric {*/
public class ChatAnimation implements ModInitializer {
/*?}*/

    public static final String MOD_ID = "chatanimation";
    public static final String MOD_NAME = "ChatAnimation";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final String CONFIG_FILE = "chatanimation.json";

    /*? if forge {*/
	/*//? if 1.20.2 {
	/^public ChatAnimation() {
	^///? } else {
    public ChatAnimation(final FMLJavaModLoadingContext context) {
	//? }
        File configFile = FMLPaths.CONFIGDIR.get().resolve(ChatAnimation.CONFIG_FILE).toFile();
        ModConfig.getConfig().load(configFile);

		//? if 1.20.2 {
		/^net.minecraftforge.fml.ModLoadingContext.get().registerExtensionPoint(
		^///? } else {
		context.registerExtensionPoint(
		//? }
			ConfigScreenHandler.ConfigScreenFactory.class,
			() -> new ConfigScreenHandler.ConfigScreenFactory((c, parent) -> ModConfigScreen.create(parent))
		);
    }
    *//*?}*/

    /*? if neoforge {*/
    /*public ChatAnimation(ModContainer container) {
        File configFile = FMLPaths.CONFIGDIR.get().resolve(ChatAnimation.CONFIG_FILE).toFile();
        ModConfig.getConfig().load(configFile);

        container.registerExtensionPoint(IConfigScreenFactory.class, (c, parent) -> ModConfigScreen.create(parent));
    }
    *//*?}*/

    /*? if fabric {*/
    @Override
    public void onInitialize() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), ChatAnimation.CONFIG_FILE);
        ModConfig.getConfig().load(configFile);
    }
    /*?}*/

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

	public static double getOpacityFactor(int ticksAlive) {
		if (!ModConfig.getConfig().enableMessageAnimation || !ModConfig.getConfig().enableOpacity) {
			return 1;
		}
		float fadeTimeInTicks = (float) ModConfig.getConfig().fadeTimeMessage / 50.0F;
		if (fadeTimeInTicks <= 0) return 1;
		return Math.min((float) ticksAlive / fadeTimeInTicks, 1.0F);
	}
}
