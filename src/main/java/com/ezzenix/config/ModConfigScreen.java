package com.ezzenix.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModConfigScreen extends Screen {

	Screen parentScreen;
	ModConfig config;

	protected ModConfigScreen(Screen parentScreen) {
		super(Text.literal("Settings"));
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
		int y = this.height / 2 - buttonHeight / 2;

		int spacingX = 80;
		int spacingY = 25;

		addToggleOption(x-spacingX, y-spacingY*3, buttonWidth, buttonHeight, "Animate Messages", () -> config.enableMessageAnimation, (val) -> config.enableMessageAnimation = val);

		addSliderOption(x-spacingX, y-spacingY*2, buttonWidth, buttonHeight, "Duration", 10, 300, () -> config.fadeTimeMessage, (val) -> config.fadeTimeMessage = val);

		addToggleOption(x-spacingX, y-spacingY, buttonWidth, buttonHeight, "Hide Indicator", () -> config.removeMessageIndicator, (val) -> config.removeMessageIndicator = val);

		addToggleOption(x+spacingX, y-spacingY*3, buttonWidth, buttonHeight, "Animate Open", () -> config.enableTextFieldAnimation, (val) -> config.enableTextFieldAnimation = val);

		addSliderOption(x+spacingX, y-spacingY*2, buttonWidth, buttonHeight, "Duration", 10, 700, () -> config.fadeTimeTextField, (val) -> config.fadeTimeTextField = val);

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Reset").formatted(Formatting.RED), button -> {
			config.reset();
			this.clearChildren();
			this.init();
		}).dimensions(x-spacingX, y+spacingY*3, buttonWidth, buttonHeight).build());

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Done").formatted(Formatting.GREEN), button -> {
			this.close();
		}).dimensions(x+spacingX, y+spacingY*3, buttonWidth, buttonHeight).build());
	}

	private void addToggleOption(int x, int y, int width, int height, String text, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		ButtonWidget widget = ButtonWidget.builder(Text.literal(text + ": ").append((getter.get() ? Text.literal("ON").formatted(Formatting.GREEN) : Text.literal("OFF").formatted(Formatting.RED))), button -> {
			setter.accept(!getter.get());
			button.setMessage(Text.literal(text + ": ").append((getter.get() ? Text.literal("ON").formatted(Formatting.GREEN) : Text.literal("OFF").formatted(Formatting.RED))));
		}).dimensions(x, y, width, height).build();

		this.addDrawableChild(widget);
	}

	private void addSliderOption(int x, int y, int width, int height, String label, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter) {
		int current = getter.get();
		double normalized = (double)(current - min) / (max - min);

		SliderWidget widget = new SliderWidget(x, y, width, height,
			Text.literal(label + ": " + getter.get() + "ms"),
			normalized) {

			@Override
			protected void updateMessage() {
				int valueInt = (int) (this.value * (max - min)) + min;
				setMessage(Text.literal(label + ": " + valueInt + "ms"));
			}

			@Override
			protected void applyValue() {
				int newValue = (int) (this.value * (max - min)) + min;
				setter.accept(newValue);
			}
		};

		this.addDrawableChild(widget);
	}

	public void close() {
		config.save();
		if (this.client != null) {
			this.client.setScreen(parentScreen);
		}
	}
}
