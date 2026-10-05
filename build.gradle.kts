import gg.meza.stonecraft.mod
import java.net.URI
import java.nio.file.Files

plugins {
    id("gg.meza.stonecraft")
	id("com.ezzenix.mcverify") version "0.1.0"
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
	displayName = mod.version

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
		if (mod.hasProp("supported_to")) {
			minecraftVersionRange {
				start = mod.minecraftVersion
				end = mod.prop("supported_to")
			}
		} else {
			minecraftVersionList(mod.minecraftVersion)
		}
    }

    curseforge {
		accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
		projectId = "892086"
        client = true
        server = false
		if (mod.isFabric) {
			optional("modmenu")
		}
		if (mod.hasProp("supported_to")) {
			minecraftVersionRange {
				start = mod.minecraftVersion
				end = mod.prop("supported_to")
			}
		} else {
			minecraftVersionList(mod.minecraftVersion)
		}
    }
}

repositories {
	maven("https://maven.terraformersmc.com/")
	maven("https://ezzenix.github.io/emlib")
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

	/* mixinextras already exists either in fabric or emlib */
	compileOnly("io.github.llamalad7:mixinextras-common:0.5.4")
	annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.4")

	val emlib = "com.ezzenix:emlib:1.0.3+${mod.prop("deps.emlib")}-${mod.loader}-SNAPSHOT";
	add(implementationConfiguration, emlib)
	include(emlib)

	if (mod.isFabric && mod.hasProp("deps.modmenu")) {
		add(implementationConfiguration, "com.terraformersmc:modmenu:${mod.prop("deps.modmenu")}")
	}
}

loom {
	if (mod.isForge) {
		forge {
			mixinConfig("${mod.id}.mixins.json")
		}
	}
}

gradle.projectsEvaluated {
	allprojects.filter { it.tasks.names.contains("runClient") }.forEach { project ->
		tasks.register("Run ${project.name}") {
			dependsOn(project.tasks.named("runClient"))
			group = "runs"
		}
	}
}

mcverify {
	loader = mod.loader
	serverAddress = "localhost"
	if (mod.hasProp("supported_to")) {
		versionRange {
			start = mod.minecraftVersion
			end = mod.prop("supported_to")
		}
	} else {
		version = mod.minecraftVersion
	}
}
