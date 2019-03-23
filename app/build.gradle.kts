import com.android.build.gradle.internal.dsl.TestOptions
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("realm-android")
    id("deploygate")
    id("com.github.ben-manes.versions")
    id("io.fabric")
    id("de.mannodermaus.android-junit5")
    // Apply at the bottom
    id("com.google.gms.google-services") apply false
}

apply {
    from("lint-checks.gradle")
    from("$rootDir/core_dependencies.gradle")
}

android {
    compileSdkVersion(Versions.compileSdkVersion)

    dataBinding.isEnabled = true

    testOptions {
        unitTests(closureOf<TestOptions.UnitTestOptions> {
            isIncludeAndroidResources = true
        })
    }


    signingConfigs {
        getByName("debug") {
            storeFile = file("../debug.keystore")
        }
        create("release") {
            storeFile = file("../SunsetScrob.jks")
            storePassword = System.getenv("SUNSET_STORE_PASSWORD")
            keyAlias = System.getenv("SUNSET_KEY_ALIAS")
            keyPassword = System.getenv("SUNSET_KEY_PASSWORD")
        }
    }
    defaultConfig {
        applicationId = "com.mataku.scrobscrob"

        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
        versionCode = 23
        versionName = "0.2.3"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        buildConfigField("String", "API_KEY", "${rootProject.ext["API_KEY"]}")
        buildConfigField("String", "SHARED_SECRET", "${rootProject.ext["SHARED_SECRET"]}")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            applicationIdSuffix = ".dev"
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    lintOptions {
        isAbortOnError = false
        textReport = true
        textOutput("stdout")
        xmlReport = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:licenses"))

    implementation(Deps.cardView)
    implementation(Deps.lifecycleExtensions)
    implementation(Deps.materialComponent)
    implementation(Deps.preference)

    implementation(Deps.okhttp)
    implementation(Deps.okhttpLoggingInterceptor)

    implementation(Deps.kotlinCoroutinesAndroid)
    implementation(Deps.kotlinCoroutinesCore)

    implementation(Deps.retrofit)
    implementation(Deps.retrofitMoshiConverter)

    implementation(Deps.rxjava)

    implementation(Deps.epoxy)
    implementation(Deps.epoxyDatabinding)
    kapt(Deps.epoxyProcessor)

    implementation(Deps.moshi)
    implementation(Deps.moshiKotlin)

    implementation(Deps.retrofitKotlinCoroutinesAdapter)
    implementation(Deps.firebaseCore)
    implementation(Deps.crashlytics)

    implementation(Deps.roomRuntime)
    kapt(Deps.roomCompiler)

    implementation(Deps.koinAndroid)
    implementation(Deps.koinAndroidXScope)
    implementation(Deps.koinAndroidXViewModel)

    implementation(Deps.glide)
    kapt(Deps.glideCompiler)

    testImplementation(Deps.junit)
    testImplementation(Deps.guava)
    testImplementation(Deps.kotlinTestJunit)
    testImplementation(Deps.robolectric)
    testImplementation(Deps.mockWebServer)

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")

    testImplementation("org.junit.jupiter:junit-jupiter-params:5.3.1")

    testImplementation("androidx.test:core:1.0.0")
    testImplementation(Deps.spek)
    testImplementation(Deps.spekJunitPlatformEngine)
    testImplementation(Deps.kotlinReflect)
    testImplementation("org.junit.platform:junit-platform-runner:1.1.0")
}

repositories {
    google()
    jcenter()
    mavenCentral()
    maven("https://maven.google.com")
}

kapt {
    correctErrorTypes = true
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

val dependencyUpdates by tasks.getting(DependencyUpdatesTask::class) {
    val rejectPatterns = listOf("alpha", "beta", "rc", "cr", "m").map { qualifier ->
        Regex("(?i).*[.-]$qualifier[.\\d-]*")
    }
    resolutionStrategy = closureOf<ResolutionStrategy> {
        componentSelection {
            all {
                if (rejectPatterns.any { it.matches(this.candidate.version) }) {
                    this.reject("Release candidate")
                }
            }
        }
    }
}

tasks.withType<Test> {
    maxParallelForks = 2
    failFast = true
}

apply(mapOf("plugin" to "com.google.gms.google-services"))