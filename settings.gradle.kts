pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
	id("gg.meza.stonecraft") version "1.12.2"
	id("dev.kikugie.stonecutter") version "0.9.6"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    shared {
        fun mc(version: String, vararg loaders: String) {
            for (it in loaders) version("$version-$it", version)
        }

        mc("1.20.1", "fabric", "forge")
        mc("1.21.1", "fabric", "forge", "neoforge")
        mc("1.21.10", "fabric", "forge", "neoforge")
        mc("1.21.11", "fabric", "neoforge")
		mc("26.1", "fabric", "neoforge")
		mc("26.2", "fabric", "neoforge")

        vcsVersion = "26.2-fabric"
    }
    create(rootProject)
}

rootProject.name = "chatanimation"
