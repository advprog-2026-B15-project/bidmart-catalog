val seleniumJavaVersion = "4.14.1"
val seleniumJupiterVersion = "5.0.1"
val webdrivermanagerVersion = "5.6.3"
val junitJupiterVersion = "5.9.1"

plugins {
    java
    jacoco
    pmd
    checkstyle
    id("org.springframework.boot") version "3.5.11"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "4.4.1.3373"
    id("org.owasp.dependencycheck") version "9.0.7"
    kotlin("jvm")
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"
description = "bidmartcatalog"

pmd {
    isConsoleOutput = true
    toolVersion = "6.55.0"
    ruleSets = listOf("category/java/errorprone.xml", "category/java/bestpractices.xml")
}

checkstyle {
    toolVersion = "10.12.5"
}

tasks.withType<Checkstyle>().configureEach {
    ignoreFailures = true
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal()
            }
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")

    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(kotlin("stdlib-jdk8"))

    // Database & ORM
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    // OpenAPI / Swagger UI untuk dokumentasi API
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // Library utama untuk integrasi RabbitMQ
    implementation ("org.springframework.boot:spring-boot-starter-amqp")

    // Library Jackson untuk konversi pesan JSON ke Object Java
    implementation ("com.fasterxml.jackson.core:jackson-databind")
    // Caching (Caffeine in-process) ─────────────────────────────────────────
    implementation ("org.springframework.boot:spring-boot-starter-cache")
    implementation ("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Observability (Milestone 100)
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumJavaVersion")
    testImplementation("io.github.bonigarcia:selenium-jupiter:$seleniumJupiterVersion")
    testImplementation("io.github.bonigarcia:webdrivermanager:$webdrivermanagerVersion")
//    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

tasks.register<Test>("unitTest") {
    description = "Runs unit tests."
    group = "verification"

    filter {
        excludeTestsMatching("*FunctionalTest")
    }
}

tasks.register<Test>("functionalTest") {
    description = "Runs functional tests."
    group = "verification"

    filter {
        includeTestsMatching("*FunctionalTest")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.test {
    filter {
        excludeTestsMatching("*FunctionalTest")
    }

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
