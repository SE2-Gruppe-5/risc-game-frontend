plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    jacoco
    id("org.sonarqube") version "5.1.0.4882"
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
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    testOptions {
        unitTests {
            all {
                it.finalizedBy(tasks.named("createDebugCoverageReport"))
                it.finalizedBy(tasks.named("jacocoTestReport"))
            }
        }
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation(libs.kotlinxCoroutines)
    implementation(libs.okhttp)
    implementation(libs.okhttpeventsource)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.core.ktx)

    testImplementation(libs.junit)
    //testImplementation(libs.mockito)
    //testImplementation(libs.mockito.kotlin)

    //androidTestImplementation(libs.junit)
    //androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}



tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest", "testReleaseUnitTest", "connectedDebugAndroidTest")
    dependsOn("createDebugCoverageReport", "createDebugAndroidTestCoverageReport","createDebugUnitTestCoverageReport")

    reports {
        xml.required = true
        csv.required = false
        html.required = true
    }

    val srcDirs = listOf(
        "${project.projectDir}/src/main/java",
        "${project.projectDir}/src/main/kotlin"
    )
    val classDirs = listOf(
        "${project.layout.buildDirectory.get().asFile}/tmp/kotlin-classes/debug",
        "${project.layout.buildDirectory.get().asFile}/intermediates/javac/debug",
        "${project.layout.buildDirectory.get().asFile}/build/tmp/kotlin-classes/debug"
    )
    val execData = files(
        fileTree("${project.layout.buildDirectory.get().asFile}/jacoco") { include("*.exec") },
        fileTree("${project.layout.buildDirectory.get().asFile}/outputs/code_coverage") { include("*.ec") },
        fileTree("${project.layout.buildDirectory.get().asFile}/outputs/connected_android_test_additional_output") { include("*.ec") },
        fileTree("${project.layout.buildDirectory.get().asFile}/outputs/unit_test_code_coverage") { include("*.exec") }
    )
    //execData.files.forEach {
    //    println("Coverage file: ${it.absolutePath}")
    //}
    sourceDirectories.setFrom(files(srcDirs))
    classDirectories.setFrom(files(classDirs))
    executionData.setFrom(files(execData))
}

sonar {
    properties {
        property("sonar.projectKey", "SE2-Gruppe-5_game-project-frontend")
        property("sonar.organization", "se2-gruppe-5")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths",
            "${project.layout.buildDirectory.get().asFile}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml," +
            "${project.layout.buildDirectory.get().asFile}/outputs/code_coverage/androidTest/debug/connected/report.xml")
    }
}