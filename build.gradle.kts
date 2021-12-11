import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    application
}

group = "me.vincent.emergencyfood"
version = "1.4.2"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation("net.dv8tion:JDA:4.3.0_346")
    implementation ("com.sedmelluq:lavaplayer:1.3.77")
    implementation("org.json:json:20210307")
    implementation ("org.jsoup:jsoup:1.14.3")
    implementation("io.ktor:ktor-client-core:1.6.4")
    implementation("io.ktor:ktor-client-cio:1.6.4")
    implementation("ch.qos.logback:logback-classic:1.2.7")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.30")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
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
    mainClass.set("me.vincentvibe3.emergencyfood.core.MainKt")
}

project.gradle.startParameter.excludedTaskNames.add("jar")