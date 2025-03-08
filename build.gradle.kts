plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.allopen") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    id("io.quarkus")
    id("org.jetbrains.dokka") version "1.9.20"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    // Core dependencies
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-client-kotlin-serialization")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    
    // Kotlin coroutines support
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.7.3")
    
    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    
    // Database (to be used later)
    implementation("io.quarkus:quarkus-hibernate-reactive-panache")
    implementation("io.quarkus:quarkus-reactive-pg-client")
    
    // PostgreSQL JDBC driver
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    
    // Security (to be used later)
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-elytron-security-jdbc")
    
    // Validation
    implementation("io.quarkus:quarkus-hibernate-validator")
    
    // Messaging (to be used later)
    implementation("io.quarkus:quarkus-smallrye-reactive-messaging")
    implementation("io.quarkus:quarkus-smallrye-reactive-messaging-kafka")
    
    // OpenAPI documentation
    implementation("io.quarkus:quarkus-smallrye-openapi")
    
    // Metrics and observability
    implementation("io.quarkus:quarkus-micrometer-registry-prometheus")
    implementation("io.quarkus:quarkus-smallrye-health")
    
    // Dokka dependencies
    dokkaPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.9.20")
    
    // Testing
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.quarkus:quarkus-test-security")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")

    implementation("io.quarkus:quarkus-smallrye-reactive-messaging-kafka:3.15.3.1")
    // https://mvnrepository.com/artifact/io.quarkus/quarkus-smallrye-reactive-messaging
    implementation("io.quarkus:quarkus-smallrye-reactive-messaging:3.15.3.1")


}

group = "solutions.dreamforge"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("io.quarkus.arc.ApplicationScoped")
    annotation("javax.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
    annotation("javax.inject.Singleton")
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
        javaParameters = true
    }
}

// Dokka configuration
tasks.dokkaHtml {
    outputDirectory.set(file("${layout.buildDirectory.get()}/dokka"))
    
    // Configure Dokka options
    dokkaSourceSets {
        named("main") {
            moduleName.set("Quarkus Backend API")
            
            // Package configuration for better navigation
            perPackageOption {
                matchingRegex.set("solutions.dreamforge.*")
                skipDeprecated.set(false)
                reportUndocumented.set(true)
                includeNonPublic.set(false)
            }
            
            // Source links for GitHub (uncomment and adjust if needed)
            // sourceLink {
            //     localDirectory.set(file("src/main/kotlin"))
            //     remoteUrl.set(uri("https://github.com/yourusername/yourrepo/tree/main/src/main/kotlin").toURL())
            //     remoteLineSuffix.set("#L")
            // }
        }
    }
}

// Create a task that depends on dokkaHtml and adds it to the build process
tasks.register("generateDocs") {
    dependsOn("dokkaHtml")
    group = "documentation"
    description = "Generates project documentation using Dokka"
}
