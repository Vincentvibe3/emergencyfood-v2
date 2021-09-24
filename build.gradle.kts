import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    application
}

group = "me.vincent.emergencyfood"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation("net.dv8tion:JDA:4.3.0_330")
    implementation ("com.sedmelluq:lavaplayer:1.3.77")
    implementation("org.json:json:20210307")
    implementation ("org.jsoup:jsoup:1.14.2")
    implementation("com.github.kittinunf.result:result-jvm:5.2.0")
    implementation ("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation ("com.github.kittinunf.fuel:fuel-coroutines:2.3.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.30")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}


tasks.withType<ShadowJar>{
    archiveFileName.set("Emergencyfood-${project.version}.jar")
}

application {
    mainClass.set("me.vincentvibe3.emergencyfood.core.MainKt")
}