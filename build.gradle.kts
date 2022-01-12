import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    application
}

group = "io.github.vincent.emergencyfood"
version = "1.5.0"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven ( "https://jitpack.io" )
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.3")
    implementation ("com.sedmelluq:lavaplayer:1.3.77")
    implementation("org.json:json:20210307")
    implementation ("org.jsoup:jsoup:1.14.3")
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-cio:1.6.7")
    implementation("ch.qos.logback:logback-classic:1.2.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.30")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
    implementation("com.github.Vincentvibe3:ef-player:v.1.2.8")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.31")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<ShadowJar>{
    archiveFileName.set("Emergencyfood-${project.version}.jar")
}

application {
    mainClass.set("io.github.vincentvibe3.emergencyfood.core.MainKt")
}

project.gradle.startParameter.excludedTaskNames.add("jar")