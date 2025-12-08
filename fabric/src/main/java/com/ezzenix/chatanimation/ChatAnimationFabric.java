package com.ezzenix.chatanimation;

import com.ezzenix.chatanimation.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class ChatAnimationFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), ChatAnimation.CONFIG_FILE);
        ModConfig.getConfig().load(configFile);

    }
}
