plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    jacoco
    id("org.sonarqube") version "6.0.1.5171"
}

android {
    namespace = "com.se2gruppe5.risikofrontend"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.se2gruppe5.risikofrontend"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests {
            all {
                it.finalizedBy(tasks.named("jacocoTestReport"))
            }
        }
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("testDebugUnitTest"))

    reports {
        xml.required = true
        csv.required = false
        html.required = false
    }

    val srcDirs = listOf(
        "${project.projectDir}/src/main/java",
        "${project.projectDir}/src/main/kotlin"
    )
    val classDirs = listOf(
        "${project.layout.buildDirectory.get().asFile}/tmp/kotlin-classes/debug",
        "${project.layout.buildDirectory.get().asFile}/intermediates/javac/debug"
    )
    val execData = listOf(
        "${project.layout.buildDirectory.get().asFile}/jacoco/testDebugUnitTest.exec",
        "${project.layout.buildDirectory.get().asFile}/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
    )

    sourceDirectories.setFrom(files(srcDirs))
    classDirectories.setFrom(files(classDirs))
    executionData.setFrom(files(execData))
}

sonar {
    properties {
        property("sonar.projectKey", "SE2-Gruppe-5_game-project-frontend")
        property("sonar.organization", "se2-gruppe-5")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}