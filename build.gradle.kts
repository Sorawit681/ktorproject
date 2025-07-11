val kotlin_version: String by project
val logback_version: String by project
//val ktor_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.2.1"
    kotlin("plugin.serialization") version "2.1.10"
}

group = "com.tracker"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-auth:3.2.1")
    implementation("io.ktor:ktor-server-auth-ldap:3.2.1")
    implementation("io.ktor:ktor-server-auth:3.2.1")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
//    testImplementation("io.ktor:ktor-server-tests")
//    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
