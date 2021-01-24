import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.github.ben-manes.versions")
    // Apply at the bottom
    id("com.google.gms.google-services") apply false
}

apply {
    from("lint-checks.gradle")
    from("$rootDir/core_dependencies.gradle")
    from("$rootDir/test_dependencies.gradle")
    from("$rootDir/test_options.gradle")
}

android {
    compileSdkVersion(Versions.compileSdkVersion)

    dataBinding.isEnabled = true

    defaultConfig {
        applicationId = "com.mataku.scrobscrob"
        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
        versionCode = 30
        versionName = "0.3.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        buildConfigField("String", "API_KEY", "${rootProject.ext["API_KEY"]}")
        buildConfigField("String", "SHARED_SECRET", "${rootProject.ext["SHARED_SECRET"]}")
        proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isMinifyEnabled = true
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

    packagingOptions {
        exclude("META-INF/atomicfu.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-io.kotlin_module")
        exclude("META-INF/kotlinx-io.kotlin_module")
        exclude("META-INF/ktor-client-json.kotlin_module")
        exclude("META-INF/ktor-client-core.kotlin_module")
        exclude("META-INF/ktor-http.kotlin_module")
        exclude("META-INF/ktor-utils.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-core.kotlin_module")
        exclude("META-INF/kotlinx-serialization-runtime.kotlin_module")
    }

    dynamicFeatures = mutableSetOf(":feature:licenses")
}

dependencies {
    implementation(project(":core"))

    implementation(Deps.cardView)
    implementation(Deps.lifecycleExtensions)
    implementation(Deps.materialComponent)
    implementation(Deps.preference)

    implementation(Deps.kotlinCoroutinesAndroid)

    implementation(Deps.epoxy)
    implementation(Deps.epoxyDatabinding)
    kapt(Deps.epoxyProcessor)

    implementation(Deps.firebaseCore)

    implementation(Deps.roomRuntime)
    kapt(Deps.roomCompiler)

    implementation(Deps.koinAndroid)
    implementation(Deps.koinAndroidXScope)
    implementation(Deps.koinAndroidXViewModel)

    implementation(Deps.glide)
    kapt(Deps.glideCompiler)

    debugImplementation("com.facebook.flipper:flipper:0.66.0")
    debugImplementation("com.facebook.soloader:soloader:0.9.0")
    debugImplementation("com.facebook.flipper:flipper-network-plugin:0.66.0")

    releaseImplementation("com.facebook.flipper:flipper-noop:0.66.0")
}

repositories {
    google()
    jcenter()
    mavenCentral()
    maven("https://maven.google.com")
    maven("https://kotlin.bintray.com/kotlinx")
}

kapt {
    correctErrorTypes = true
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

tasks.withType<Test> {
    maxParallelForks = 2
    failFast = true
}

apply(mapOf("plugin" to "com.google.gms.google-services"))
