package com.ezzenix.chatanimation.config;

import com.ezzenix.chatanimation.ChatAnimation;
import com.ezzenix.emlib.config.EmConfig;

@EmConfig.Config(title = ChatAnimation.MOD_NAME)
public class ModConfig extends EmConfig {
	@Comment
	public static Comment _messages;
	@Option
	public static boolean enableMessageAnimation = true;
	@Requires(option="enableMessageAnimation", value="true")
	@Option(min=10, max=800, isSlider=true, suffix="ms")
	public static int fadeTimeMessage = 150;
	@Requires(option="enableMessageAnimation", value="true")
	@Option
	public static boolean enableOpacity = true;
	@Option
	public static boolean removeMessageIndicator = true;

	@Comment
	public static Comment _input;
	@Option
	public static boolean enableTextFieldAnimation = true;
	@Requires(option="enableTextFieldAnimation", value="true")
	@Option(min=10, max=800, isSlider=true, suffix="ms")
	public static int fadeTimeTextField = 170;

	@Entry(name="Show Chat Only When Typing")
	public static boolean showChatOnlyWhenTyping = false;
}
