package com.ezzenix.chatanimation;

import com.ezzenix.chatanimation.config.ModConfig;
import com.ezzenix.chatanimation.config.ModConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

@Mod(value = ChatAnimation.MOD_ID)
public class ChatAnimationForge {

    public ChatAnimationForge(FMLJavaModLoadingContext context) {
        File configFile = FMLPaths.CONFIGDIR.get().resolve(ChatAnimation.CONFIG_FILE).toFile();
        ModConfig.getConfig().load(configFile);

        context.registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory((c, parent) -> ModConfigScreen.create(parent))
        );
    }

}