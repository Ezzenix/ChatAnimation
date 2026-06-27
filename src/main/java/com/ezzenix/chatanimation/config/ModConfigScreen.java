package com.ezzenix.chatanimation.config;

import net.minecraft.ChatFormatting;
//? if <1.21 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///? }
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModConfigScreen extends Screen {

	Screen parentScreen;
	ModConfig config;

	Button resetButton;

	public ModConfigScreen(Screen parentScreen) {
		super(Component.literal("Settings"));
		this.parentScreen = parentScreen;
		this.config = ModConfig.getConfig();
	}

	public static Screen create(Screen parentScreen) {
		return new ModConfigScreen(parentScreen);
	}
	
    @Override
	//? if >=26.1 {
	public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int i, int j, float f) {
	//? } else {
    /*public void render(@NotNull GuiGraphicsExtractor graphics, int i, int j, float f) {
	*///? }
		//? if <1.21 {
        /*this.renderBackground(graphics);
		*///?}

		int y = this.height / 2 - 100;
		graphics.centeredText(this.font, "Messages", this.width / 2 - 80, y, -1);
		graphics.centeredText(this.font, "Input Field", this.width / 2 + 80, y, -1);

		//? if >=26.1 {
		super.extractRenderState(graphics, i, j, f);
		//? } else {
		/*super.render(graphics, i, j, f);
		 *///? }

		this.resetButton.active = !config.isDefault();
    }

	@Override
	protected void init() {
		int buttonWidth = 140;
		int buttonHeight = 20;

		int x = this.width / 2 - buttonWidth / 2;
		int y = this.height / 2 - buttonHeight / 2 - 75;

		int spacingX = 80;
		int spacingY = 25;

		addToggleOption(x-spacingX, y, buttonWidth, buttonHeight, "Enabled", () -> config.enableMessageAnimation, (val) -> config.enableMessageAnimation = val);
		addSliderOption(x-spacingX, y+spacingY, buttonWidth, buttonHeight, "Duration", 10, 400, () -> config.fadeTimeMessage, (val) -> config.fadeTimeMessage = val);
		addToggleOption(x-spacingX, y+spacingY*2, buttonWidth, buttonHeight, "Hide Indicator", () -> config.removeMessageIndicator, (val) -> config.removeMessageIndicator = val);
		addToggleOption(x-spacingX, y+spacingY*3, buttonWidth, buttonHeight, "Fade Opacity", () -> config.enableOpacity, (val) -> config.enableOpacity = val);

		addToggleOption(x+spacingX, y, buttonWidth, buttonHeight, "Enabled", () -> config.enableTextFieldAnimation, (val) -> config.enableTextFieldAnimation = val);
		addSliderOption(x+spacingX, y+spacingY, buttonWidth, buttonHeight, "Duration", 10, 700, () -> config.fadeTimeTextField, (val) -> config.fadeTimeTextField = val);

		this.resetButton = Button.builder(Component.literal("Reset"), button -> {
			config.reset();
			this.clearWidgets();
			this.init();
		}).bounds(x-spacingX, y+spacingY*6, buttonWidth, buttonHeight).build();
		this.addRenderableWidget(resetButton);

		this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> {
			this.minecraft.gui.setScreen(parentScreen);
        }).bounds(x+spacingX, y+spacingY*6, buttonWidth, buttonHeight).build());
	}

	private Component buildToggleLabel(String text, Supplier<Boolean> getter) {
		return Component.literal(text + ": ")
			.append(getter.get()
				? Component.literal("Yes").withStyle(ChatFormatting.GREEN)
				: Component.literal("No").withStyle(ChatFormatting.RED));
	}

	private void addToggleOption(int x, int y, int width, int height, String text, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		Button widget = Button.builder(buildToggleLabel(text, getter), button -> {
			setter.accept(!getter.get());
			button.setMessage(buildToggleLabel(text, getter));
		}).bounds(x, y, width, height).build();

		this.addRenderableWidget(widget);
	}

	private Component buildSliderLabel(String text, String suffix, ChatFormatting style, int value) {
		return Component.literal(text + ": ")
			.append(Component.literal(value + suffix).withStyle(style));
	}

	private void addSliderOption(int x, int y, int width, int height, String label, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter) {
		int current = getter.get();
		double normalized = (double)(current - min) / (max - min);

		AbstractSliderButton widget = new AbstractSliderButton(x, y, width, height,
			buildSliderLabel(label, "ms", ChatFormatting.YELLOW, getter.get()),
			normalized) {

			@Override
			protected void updateMessage() {
				int valueInt = (int) (this.value * (max - min)) + min;
				setMessage(buildSliderLabel(label, "ms", ChatFormatting.YELLOW, valueInt));
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
		this.minecraft.gui.setScreen(parentScreen);
	}
}
