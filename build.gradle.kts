plugins {
    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
}

group = "com.volodya"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")

    implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:3.11.0"))
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")

    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.30.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.30.2")
    implementation("org.jetbrains.exposed:exposed-java-time:0.30.2")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }
    test {
        useJUnitPlatform()
    }
}