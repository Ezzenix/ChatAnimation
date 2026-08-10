package com.ezzenix.chatanimation.config;

import com.ezzenix.chatanimation.lib.config.BaseConfig;

@BaseConfig.Config(title="Chat Animation Config")
public class ModConfig extends BaseConfig {
	@Comment(name="Messages")
	public static Comment _messages;
	@Entry(name="Animate Messages")
	public static boolean enableMessageAnimation = true;
	@Entry(name="Duration", min=10, max=800, suffix="ms")
	public static int fadeTimeMessage = 150;
	@Entry(name="Hide Indicator Line", desc="Hide the indicator line on the left of messages.")
	public static boolean removeMessageIndicator = true;
	@Entry(name="Animate Opacity")
	public static boolean enableOpacity = true;

	@Comment(name="Input Field")
	public static Comment _input;
	@Entry(name="Animate Input Field")
	public static boolean enableTextFieldAnimation = true;
	@Entry(name="Duration", min=10, max=800, suffix="ms")
	public static int fadeTimeTextField = 170;

	@Entry(name="Show Chat Only When Typing")
	public static boolean showChatOnlyWhenTyping = false;
}
