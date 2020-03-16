import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation

plugins {
    kotlin("jvm") version "1.3.70"

    id("com.github.johnrengelman.shadow") version "5.2.0"
}

repositories {
    maven("http://ci.athion.net/job/FastAsyncWorldEdit/ws/mvn/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    compileOnly("com.destroystokyo.paper:paper-api:+")
    implementation("co.aikar:acf-paper:+")

    compileOnly("com.boydti:fawe-api:+")
}

tasks {
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
