import gg.meza.stonecraft.mod
import java.net.URI
import java.nio.file.Files

plugins {
    id("gg.meza.stonecraft")
}

val isDeobfuscated = stonecutter.current.parsed >= "26.1"

modSettings {
    clientOptions {
        fov = 90
        guiScale = 2
        narrator = false
        darkBackground = true
        musicVolume = 0.0
    }
}

fun fetchLatestChangelog() : String {
	val str = Files.readString(layout.settingsDirectory.file("CHANGELOG.md").asFile.toPath())
	val first = str.indexOf("## ")
	val i = str.indexOf('\n', first) + 2
	var r = str.indexOf("\n## ", i + 1)
	if (r == -1) r = str.length
	return str.substring(i, r - 1)
}

publishMods {
	dryRun = false
	changelog = fetchLatestChangelog()

	if (mod.isFabric) {
		modLoaders.add("quilt")
	}

    modrinth {
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")
		projectId = "DnNYdJsx"
		environment = CLIENT_ONLY
		if (mod.isFabric) {
			optional("modmenu")
		}
		minecraftVersionList(mod.prop("supported_versions", mod.prop("minecraft_version")))
    }

    curseforge {
		accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
		projectId = "892086"
        client = true
        server = false
		if (mod.isFabric) {
			optional("modmenu")
		}
		minecraftVersionList(mod.prop("supported_versions", mod.prop("minecraft_version")))
    }
}

repositories {
    maven { name = "Terraformers"; url = URI("https://maven.terraformersmc.com/") }
}

dependencies {
	val implementationConfiguration = when {
		isDeobfuscated -> "implementation"
		else -> "modImplementation"
	}
	val apiConfiguration = when {
		isDeobfuscated -> "api"
		else -> "modApi"
	}

	compileOnly("io.github.llamalad7:mixinextras-common:0.5.4")
	annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.4")
	if (mod.isForge) {
		implementation("io.github.llamalad7:mixinextras-forge:0.5.4")
		include("io.github.llamalad7:mixinextras-forge:0.5.4")
	}
	if (mod.isNeoforge) {
		implementation("io.github.llamalad7:mixinextras-neoforge:0.5.4")
		include("io.github.llamalad7:mixinextras-neoforge:0.5.4")
	}

	if (mod.isFabric) {
		if (mod.hasProp("deps.modmenu")) {
			add(implementationConfiguration, "com.terraformersmc:modmenu:${mod.prop("deps.modmenu")}")
		}
	}
}

loom {
	if (mod.isForge) {
		forge {
			mixinConfig("${mod.id}.mixins.json")
		}
	}
}
