import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.graalvm.buildtools.native") version "0.9.4"
    application
}

group = "io.github.vincent.emergencyfood"
version = "1.7.11"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven ( "https://jitpack.io" )
    gradlePluginPortal()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.16")
    implementation ("com.sedmelluq:lavaplayer:1.3.77")
    implementation("org.json:json:20220320")
    implementation ("org.jsoup:jsoup:1.15.3")
    implementation("ch.qos.logback:logback-classic:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.github.Vincentvibe3:ef-player:v.1.3.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.31")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<ShadowJar>{
    archiveFileName.set("Emergencyfood-${project.version}.jar")
}

application {
    mainClass.set("io.github.vincentvibe3.emergencyfood.core.MainKt")
}

project.gradle.startParameter.excludedTaskNames.add("jar")
