import org.gradle.jvm.tasks.Jar
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import java.time.Instant
import java.time.format.DateTimeFormatter

plugins {
    java
    idea
    `maven-publish`
    id("net.neoforged.moddev.legacy") version "2.0.61-beta-pr-118-legacy"
}

val minecraftVersion: String by project
val minecraftVersionRange: String by project
val forgeVersion: String by project
val forgeVersionRange: String by project
val loaderVersionRange: String by project
val parchmentMcVersion: String by project
val parchmentVersion: String by project
val modId: String by project
val modName: String by project
val modLicense: String by project
val modVersion: String by project
val modGroupId: String by project
val modAuthors: String by project
val modDescription: String by project
val mixins: String by project
val mixinRefMap: String by project

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven("https://cursemaven.com")
        }
        filter {
            includeGroup("curse.maven")
        }
    }

    // EMI
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }

    // REI
    maven {
        url = uri("https://maven.shedaniel.me/")
        content {
            includeGroup("me.shedaniel")
            includeGroup("me.shedaniel.cloth")
            includeGroup("dev.architectury")
        }
    }

    // JEI
    maven {
        name = "Jared's maven"
        url = uri("https://maven.blamejared.com/")
    }

    // Mirrors
    maven {
        url = uri("https://modmaven.dev/")
    }

    // Local folder "libs", if you need
    flatDir {
        dir("libs")
    }
}

base {
    archivesName = modId
    group = modGroupId
    version = modVersion
}

java {
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

sourceSets {
    main {
        resources.srcDir("src/generated/resources")
    }
    create("jei") {
        compileClasspath += sourceSets.main.get().compileClasspath
        runtimeClasspath += sourceSets.main.get().runtimeClasspath
    }
    create("rei") {
        compileClasspath += sourceSets.main.get().compileClasspath
        runtimeClasspath += sourceSets.main.get().runtimeClasspath
    }
    create("emi") {
        compileClasspath += sourceSets.main.get().compileClasspath
        runtimeClasspath += sourceSets.main.get().runtimeClasspath
    }
}

obfuscation {
    val jeiRuntimeOnly: Configuration by configurations.getting
    val reiRuntimeOnly: Configuration by configurations.getting
    val emiRuntimeOnly: Configuration by configurations.getting

    createRemappingConfiguration(jeiRuntimeOnly)
    createRemappingConfiguration(reiRuntimeOnly)
    createRemappingConfiguration(emiRuntimeOnly)
}

neoForge {
    version = "$minecraftVersion-$forgeVersion"
    runs {
        register("client-Jei") {
            client()
            sourceSet = sourceSets.getByName("jei")
        }
        register("client-Rei") {
            client()
            sourceSet = sourceSets.getByName("rei")
        }
        register("client-Emi") {
            client()
            sourceSet = sourceSets.getByName("emi")
        }
        configureEach {
            systemProperty("forge.logging.console.level", "debug")
            jvmArgument("-Xmx3000m")
            jvmArgument("-XX:+IgnoreUnrecognizedVMOptions")
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
            if (type.get().startsWith("client")) {
                programArguments.addAll("--width", "1920", "--height", "1080")
                gameDirectory = file("runs/client")
                systemProperty("mixin.debug.export", "true")
                jvmArguments.addAll(
                    "-XX:+UnlockExperimentalVMOptions",
                    "-XX:+UseG1GC",
                    "-XX:G1NewSizePercent=20",
                    "-XX:G1ReservePercent=20",
                    "-XX:MaxGCPauseMillis=50",
                    "-XX:G1HeapRegionSize=32M"
                )
            }
        }
    }
    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
    parchment {
        minecraftVersion = parchmentMcVersion
        mappingsVersion = parchmentVersion
    }
}

mixin {
    add(sourceSets.main.get(), mixinRefMap)
    for (mixin in mixins.split(",")) {
        config(mixin)
    }
}

afterEvaluate {
    tasks.withType(Jar::class).configureEach {
        manifest.attributes(
            mapOf(
                "MixinConfigs" to mixins,
                "Specification-Title" to modName,
                "Specification-Vendor" to modAuthors,
                "Specification-Version" to modVersion,
                "Implementation-Title" to modName,
                "Implementation-Version" to modVersion,
                "Implementation-Vendor" to modAuthors,
                "Implementation-Timestamp" to DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            ))
    }
}

val modJeiRuntimeOnly: Configuration by configurations.getting
val modReiRuntimeOnly: Configuration by configurations.getting
val modEmiRuntimeOnly: Configuration by configurations.getting
dependencies {
    compileOnly("org.jetbrains:annotations:24.1.0")

    //Mixins
    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")!!)
    implementation(jarJar("io.github.llamalad7:mixinextras-forge:0.4.1")!!)
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    // Any mod that is common to JEI/REI/EMI can be used with modRuntimeOnly
    modRuntimeOnly("curse.maven:jade-324717:5672013")

    val jeiVersion = "15.20.0.105"

    val reiVersion = "12.1.785"

    val emiVersion = "1.1.16+1.20.1"
    val jeiForEmi = jeiVersion // change when needed for compatibility reasons

    // Add JEI exclusive mods here if any
    modJeiRuntimeOnly("mezz.jei:jei-1.20.1-forge:${jeiVersion}")

    // Add EMI exclusive mods here (+JEI)
    modEmiRuntimeOnly("dev.emi:emi-forge:${emiVersion}")
    modEmiRuntimeOnly("curse.maven:emi-loot-681783:5760210")
    modEmiRuntimeOnly("curse.maven:fzzy-config-1005914:5835177")
    modEmiRuntimeOnly("curse.maven:kotlin-for-forge-351264:5402061")
    // Enable JEI if you need JEI + EMI
    //  modEmiRuntimeOnly("mezz.jei:jei-1.20.1-forge:${jeiForEmi}")

    // Add REI exclusive mods here
    modReiRuntimeOnly("me.shedaniel:RoughlyEnoughItems-forge:${reiVersion}") {isTransitive = false}
    modReiRuntimeOnly("curse.maven:architectury-api-419699:5137938")
    modReiRuntimeOnly("curse.maven:cloth-config-348521:5729105")
    // Enable REI Hacks if needed.
    //    modReiRuntimeOnly("curse.maven:roughly-enough-items-hacks-521393:4837449")

}

tasks {
    processResources {
        val replaceProperties = mapOf(
            "minecraft_version" to minecraftVersion,
            "minecraft_version_range" to minecraftVersionRange,
            "forge_version" to forgeVersion,
            "forge_version_range" to forgeVersionRange,
            "loader_version_range" to loaderVersionRange,
            "mod_id" to modId,
            "mod_name" to modName,
            "mod_license" to modLicense,
            "mod_version" to modVersion,
            "mod_authors" to modAuthors,
            "mod_description" to modDescription,
            "mixin_refmap" to mixinRefMap
        )

        inputs.properties(replaceProperties)
        filesMatching(listOf("META-INF/mods.toml").plus(mixins.split(","))) {
            expand(replaceProperties)
        }
    }
    compileJava {
        options.encoding = "UTF-8"
    }

    // if you want to rename the classifiers
//    jar {
//        archiveClassifier = "dev"
//    }
//    reobfJar {
//        archiveClassifier = ""
//    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components.getByName("java"))
        }
    }
    repositories {
        maven("file://$projectDir/repo")
    }
}

idea {
    project {
        jdkName = java.sourceCompatibility.toString()
        languageLevel = IdeaLanguageLevel(java.sourceCompatibility.toString())
    }
}
