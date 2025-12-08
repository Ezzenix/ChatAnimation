package com.ezzenix.chatanimation;

import com.ezzenix.chatanimation.config.ModConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuImpl implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ModConfigScreen::create;
	}

}
