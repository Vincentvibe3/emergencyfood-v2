import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.graalvm.buildtools.native") version "0.9.4"
    kotlin("plugin.serialization") version "1.8.20"
    application
}

group = "io.github.vincent.emergencyfood"
version = "1.8.9"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven ( "https://jitpack.io" )
    gradlePluginPortal()
}

dependencies {
    implementation("net.dv8tion:JDA:5.1.0")
//    implementation ("dev.arbjerg:lavaplayer:2.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0-RC")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.github.Vincentvibe3:ef-player:v.1.4.9")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<ShadowJar>{
    archiveFileName.set("Emergencyfood-${project.version}.jar")
}

application {
    mainClass.set("io.github.vincentvibe3.emergencyfood.core.MainKt")
}

project.gradle.startParameter.excludedTaskNames.add("jar")
