package com.ezzenix.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {

	private static ModConfig instance;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File(FabricLoader.getInstance()
		.getConfigDir().toFile(), "chat-animation.json");

	public static ModConfig getConfig() {
		if (instance == null) instance = new ModConfig();
		return instance;
	}

	public void load() {
		if (!CONFIG_FILE.exists()) {
			save();
			return;
		}

		try (FileReader reader = new FileReader(CONFIG_FILE)) {
			instance = GSON.fromJson(reader, ModConfig.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
			GSON.toJson(this, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		enableMessageAnimation = true;
		enableTextFieldAnimation = true;
		removeMessageIndicator = true;
		fadeTimeMessage = 150;
		fadeTimeTextField = 170;
	}

	public boolean enableMessageAnimation = true;
	public boolean enableTextFieldAnimation = true;
	public boolean removeMessageIndicator = true;
	public int fadeTimeMessage = 150;
	public int fadeTimeTextField = 170;

}
