package com.ezzenix.chatanimation;


import com.ezzenix.chatanimation.config.ModConfig;
import com.ezzenix.chatanimation.config.ModConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.io.File;

@Mod(value = ChatAnimation.MOD_ID, dist = Dist.CLIENT)
public class ChatAnimationNeoForge {

    public ChatAnimationNeoForge(ModContainer container) {
        File configFile = FMLPaths.CONFIGDIR.get().resolve(ChatAnimation.CONFIG_FILE).toFile();
        ModConfig.getConfig().load(configFile);

        container.registerExtensionPoint(IConfigScreenFactory.class, (c, parent) -> ModConfigScreen.create(parent));
    }

}