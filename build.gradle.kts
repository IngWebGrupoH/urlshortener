import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.5.5" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    kotlin("jvm") version "1.5.31" apply false
    kotlin("plugin.spring") version "1.5.31" apply false
    kotlin("plugin.jpa") version "1.5.31" apply false
}

group = "es.unizar"
version = "0.0.1-SNAPSHOT"

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    repositories {
        mavenCentral()
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }
    tasks.withType<Test> {
        useJUnitPlatform()
    }
    dependencies {
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }
}

project(":core") {
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    dependencies {
        "implementation"("org.springframework.boot:spring-boot-starter-websocket")
        "implementation"("io.micrometer:micrometer-core")// MIO
    }
    tasks.getByName<BootJar>("bootJar") {
        enabled = false
    }
 }

project(":repositories") {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    dependencies {
        "implementation"(project(":core"))
        "implementation"("org.springframework.boot:spring-boot-starter-data-jpa")
    }
    tasks.getByName<BootJar>("bootJar") {
        enabled = false
    }
}

project(":delivery") {
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    dependencies {
        "implementation"(project(":core"))
        "implementation"("org.springframework.boot:spring-boot-starter-web")
        "implementation"("org.springframework.boot:spring-boot-starter-hateoas")
        "implementation"("com.fasterxml.jackson.module:jackson-module-kotlin")
        "implementation"("commons-validator:commons-validator:1.6")
        "implementation"("org.apache.directory.studio:org.apache.commons.io:2.4")
        "implementation"("org.mockito.kotlin:mockito-kotlin:3.2.0")
        "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
        "implementation"("com.opencsv:opencsv:4.0")
        "implementation"("com.google.guava:guava:23.0")
        "implementation"("io.github.g0dkar:qrcode-kotlin:1.1.0") // MIO
        "implementation"("org.springframework.boot:spring-boot-starter-websocket")
        "implementation"("io.micrometer:micrometer-core")// MIO
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.mockito.kotlin:mockito-kotlin:3.2.0")
        //added
        "implementation" ("org.json:json:20090211")

    }
    tasks.getByName<BootJar>("bootJar") {
        enabled = false
    }
}

project(":app") {
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    dependencies {
        "implementation"(project(":core"))
        "implementation"(project(":delivery"))
        "implementation"(project(":repositories"))
        "implementation"("org.springframework.boot:spring-boot-starter")
        "implementation"("org.springframework.boot:spring-boot-starter-web")
        "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
        "implementation" ("com.google.code.gson:gson:2.8.5")
        "implementation"( "org.webjars:bootstrap:3.3.5")
        "implementation"("org.webjars:jquery:2.1.4")
        "implementation"("org.springframework.boot:spring-boot-starter-actuator")// MIO
        "implementation"("io.micrometer:micrometer-registry-prometheus")// MIO
        "implementation"("org.springframework.boot:spring-boot-starter-web") // MIO


        "runtimeOnly"("org.hsqldb:hsqldb")

        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.springframework.boot:spring-boot-starter-web")
        "testImplementation"("org.springframework.boot:spring-boot-starter-jdbc")
        "testImplementation"("org.mockito.kotlin:mockito-kotlin:3.2.0")
        "testImplementation"("com.fasterxml.jackson.module:jackson-module-kotlin")
        "testImplementation"("org.apache.httpcomponents:httpclient")
    }
}
