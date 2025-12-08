package com.ezzenix.chatanimation.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {

	private static ModConfig instance;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private transient File configFile;

	public static ModConfig getConfig() {
		if (instance == null) instance = new ModConfig();
		return instance;
	}

	public void load(File configFile) {
		this.configFile = configFile;

		if (!configFile.exists()) {
			save();
			return;
		}

		try (FileReader reader = new FileReader(configFile)) {
			instance = GSON.fromJson(reader, ModConfig.class);
			instance.configFile = configFile;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		if (configFile == null) return;
		try (FileWriter writer = new FileWriter(configFile)) {
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
