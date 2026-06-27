plugins {
    id("dev.kikugie.stonecutter")
    id("gg.meza.stonecraft")
}

stonecutter active "26.2-fabric" /* [SC] DO NOT EDIT */

stonecutter parameters {
	replacements.string(current.parsed >= "26.1") {
		replace("GuiGraphics", "GuiGraphicsExtractor")
		replace("net.minecraft.client.GuiMessage", "net.minecraft.client.multiplayer.chat.GuiMessage")
		replace("net.minecraft.client.GuiMessageTag", "net.minecraft.client.multiplayer.chat.GuiMessageTag")
		replace("graphics.drawCenteredString(", "graphics.centeredText(")
	}

	replacements.string(current.parsed >= "26.2") {
		replace("minecraft.setScreen(", "minecraft.gui.setScreen(")
		replace("Minecraft.getInstance().gui.", "Minecraft.getInstance().gui.hud.")
	}
}
