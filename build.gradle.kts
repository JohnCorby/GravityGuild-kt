import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation

plugins {
    kotlin("jvm") version "1.3.70"

    id("com.github.johnrengelman.shadow") version "5.2.0"
}

repositories {
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://jitpack.io")

//    maven("http://maven.playpro.com/")
//    maven("http://maven.enginehub.org/repo/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    compileOnly("com.destroystokyo.paper:paper-api:+")

    implementation("co.aikar:acf-paper:+")
    implementation("com.github.kangarko:GameAPI:master-SNAPSHOT")

//    compileOnly("net.coreprotect:coreprotect:+")
//    compileOnly("com.sk89q.worldedit:worldedit-bukkit:+")
}

tasks {
    defaultTasks("shadowJar")

    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    task<ConfigureShadowRelocation>("relocate") {
        target = shadowJar.get()
        prefix = project.group.toString()
    }
    shadowJar {
        dependsOn("relocate")
        minimize()
        archiveFileName.set("${project.name}.jar")
    }
}
