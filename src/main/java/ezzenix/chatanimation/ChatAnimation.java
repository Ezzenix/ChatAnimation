package ezzenix.chatanimation;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatAnimation implements ModInitializer {
    public static final String MOD_ID = "chatanimation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("ChatAnimation initialized!");
    }
}