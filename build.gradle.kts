import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
}

group = "com.kuro9"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework:spring-jms:6.1.6")

    implementation("org.apache.httpcomponents:httpclient:4.5.14")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")


    implementation("com.illposed.osc:javaosc-core:0.8")
//    implementation("org.slf4j:slf4j-api:2.0.3")
//    implementation("ch.qos.logback:logback-classic:1.4.1")

    implementation("com.smartthings.sdk:smartapp-core:0.0.4-PREVIEW")
    implementation("com.smartthings.sdk:smartthings-client:0.0.4-PREVIEW")
    implementation("com.smartthings.sdk:smartapp-spring:0.0.4-PREVIEW")
}

configurations.all {
    exclude(group = "commons-logging", module = "commons-logging")
    exclude(group = "org.slf4j", module = "slf4j-reload4j")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
