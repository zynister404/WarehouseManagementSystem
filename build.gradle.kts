plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "warehouse.app"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("mysql:mysql-connector-java:8.0.33")
}

application {
    mainClass.set("warehouse.app.MainKt")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

kotlin {
    jvmToolchain(11)
}