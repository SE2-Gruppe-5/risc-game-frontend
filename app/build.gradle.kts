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
            //^ necessary for getting coverage reports from Android-Tests (DEBUG ONLY !)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    /*
    Automatically generate appropriate Test Reports after performing Unit-Tests
    (Quality of life upgrade for running tests locally)
    */
    testOptions {
        unitTests {
            all {
                it.finalizedBy(tasks.named("jacocoTestReport"))
            }
        }
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

//Automatically generate appropriate Test Reports after performing Android-Tests
afterEvaluate { //afterEvaluate needed, as task is unknown in early stage
    tasks.named("connectedDebugAndroidTest").configure {
        finalizedBy(tasks.named("createDebugCoverageReport"))
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
    implementation(libs.dotenv.kotlin)
    //--------------------------------------------------------
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    //--------------------------------------------------------
    //androidTestImplementation(libs.junit)
    //androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents)
}

/*
 Determine correct location of environment variable file before running the test
 (This is not hardcoded, as the location of it is either
 /app/src/main/assets for Unit-Tests
 or
 /app/assets for Android-Tests (packaged)
 */
tasks.withType<Test>().configureEach {
    systemProperty("env_dir", "src/main/assets")
}

/*
 Custom Task for generating Coverage Reports for Unit-Tests
 This task is run automatically for all unit tests. (see above)
 Android-Tests use 'createDebugCoverageReport' instead.
*/
tasks.register<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required = true
        csv.required = false
        html.required = true //may be disabled, if not needed for local testing
    }
    val srcDirs = listOf(
        "${project.projectDir}/src/main/java",
        "${project.projectDir}/src/main/kotlin"
    )
    val classDirs = listOf(
        "${project.layout.buildDirectory.get().asFile}/tmp/kotlin-classes/debug",
        "${project.layout.buildDirectory.get().asFile}/intermediates/javac/debug",
    )
    val execData = files(
        fileTree("${project.layout.buildDirectory.get().asFile}/jacoco") {
            include("*.exec")
        },
        fileTree("${project.layout.buildDirectory.get().asFile}/outputs/unit_test_code_coverage") {
            include("*/*.exec") //including sub-directories
        }
    )
    sourceDirectories.setFrom(files(srcDirs))
    classDirectories.setFrom(files(classDirs))
    executionData.setFrom(files(execData))
}

// Configuration for SonarCloud
sonar {
    properties {
        property("sonar.projectKey", "SE2-Gruppe-5_game-project-frontend")
        property("sonar.organization", "se2-gruppe-5")
        property("sonar.host.url", "https://sonarcloud.io")
        //Both Unit-Tests and Android-Tests generate their own respective coverage reports
        //Merging them would be potentially difficult/tedious.
        property(
            "sonar.coverage.jacoco.xmlReportPaths", listOf(
                "${project.layout.buildDirectory.get().asFile}/reports/coverage/androidTest/debug/connected/report.xml",
                "${project.layout.buildDirectory.get().asFile}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"
            ).joinToString(",")
        )
        property("sonar.coverage.exclusions",
            "**/com/se2gruppe5/risikofrontend/game/territory/TerritoryVisualAndroid.kt, **/com/se2gruppe5/risikofrontend/game/territory/PointingArrowAndroid.kt,**/com/se2gruppe5/risikofrontend/game/dice/DiceVisualAndroid.kt, **/com/se2gruppe5/risikofrontend/lobby/**, **/com/se2gruppe5/risikofrontend/startmenu/**, **/com/se2gruppe5/risikofrontend/MainActivity.kt")
    }
}
