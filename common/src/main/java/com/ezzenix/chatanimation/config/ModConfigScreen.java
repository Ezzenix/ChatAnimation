package com.ezzenix.chatanimation.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModConfigScreen extends Screen {

	Screen parentScreen;
	ModConfig config;

	public ModConfigScreen(Screen parentScreen) {
		super(Component.literal("Settings"));
		this.parentScreen = parentScreen;
		this.config = ModConfig.getConfig();
	}

	public static Screen create(Screen parentScreen) {
		return new ModConfigScreen(parentScreen);
	}

	@Override
	protected void init() {
		int buttonWidth = 140;
		int buttonHeight = 20;

		int x = this.width / 2 - buttonWidth / 2;
		int y = this.height / 2 - buttonHeight / 2 - 75;

		int spacingX = 80;
		int spacingY = 25;

		addToggleOption(x-spacingX, y, buttonWidth, buttonHeight, "Animate Messages", () -> config.enableMessageAnimation, (val) -> config.enableMessageAnimation = val);

		addSliderOption(x-spacingX, y+spacingY, buttonWidth, buttonHeight, "Duration", 10, 300, () -> config.fadeTimeMessage, (val) -> config.fadeTimeMessage = val);

		addToggleOption(x-spacingX, y+spacingY*2, buttonWidth, buttonHeight, "Hide Indicator", () -> config.removeMessageIndicator, (val) -> config.removeMessageIndicator = val);

		addToggleOption(x+spacingX, y, buttonWidth, buttonHeight, "Animate Open", () -> config.enableTextFieldAnimation, (val) -> config.enableTextFieldAnimation = val);

		addSliderOption(x+spacingX, y+spacingY, buttonWidth, buttonHeight, "Duration", 10, 700, () -> config.fadeTimeTextField, (val) -> config.fadeTimeTextField = val);

		this.addRenderableWidget(Button.builder(Component.literal("Reset").withStyle(ChatFormatting.RED), button -> {
			config.reset();
			this.clearWidgets();
			this.init();
		}).bounds(x-spacingX, y+spacingY*5, buttonWidth, buttonHeight).build());

		this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> {
			if (this.minecraft != null) {
				this.minecraft.setScreen(null);
			}
		}).bounds(x+spacingX, y+spacingY*5, buttonWidth, buttonHeight).build());
	}

	private void addToggleOption(int x, int y, int width, int height, String text, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		Button widget = Button.builder(Component.literal(text + ": ").append((getter.get() ? Component.literal("ON").withStyle(ChatFormatting.GREEN) : Component.literal("OFF").withStyle(ChatFormatting.RED))), button -> {
			setter.accept(!getter.get());
			button.setMessage(Component.literal(text + ": ").append((getter.get() ? Component.literal("ON").withStyle(ChatFormatting.GREEN) : Component.literal("OFF").withStyle(ChatFormatting.RED))));
		}).bounds(x, y, width, height).build();

		this.addRenderableWidget(widget);
	}

	private void addSliderOption(int x, int y, int width, int height, String label, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter) {
		int current = getter.get();
		double normalized = (double)(current - min) / (max - min);

		AbstractSliderButton widget = new AbstractSliderButton(x, y, width, height,
			Component.literal(label + ": " + getter.get() + "ms"),
			normalized) {

			@Override
			protected void updateMessage() {
				int valueInt = (int) (this.value * (max - min)) + min;
				setMessage(Component.literal(label + ": " + valueInt + "ms"));
			}

			@Override
			protected void applyValue() {
				int newValue = (int) (this.value * (max - min)) + min;
				setter.accept(newValue);
			}
		};

		this.addRenderableWidget(widget);
	}

	@Override
	public void removed() {
		config.save();
	}

	@Override
	public void onClose() {
		if (this.minecraft == null) return;
		this.minecraft.setScreen(parentScreen);
	}
}
