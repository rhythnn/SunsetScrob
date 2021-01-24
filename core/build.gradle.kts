plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("kotlinx-serialization")
}

apply {
    from("$rootDir/test_dependencies.gradle")
}

android {
    compileSdkVersion(Versions.compileSdkVersion)

    defaultConfig {
        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY", "${rootProject.ext["API_KEY"]}")
        buildConfigField("String", "SHARED_SECRET", "${rootProject.ext["SHARED_SECRET"]}")
    }

    buildTypes {
        getByName("release") {
        }
        getByName("debug") {
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
    implementation(Deps.glide)
    implementation(Deps.ktorClientOkhttp)
    implementation(Deps.ktorClientJsonJvm)
    implementation(Deps.okhttp)
    implementation(Deps.okhttpLoggingInterceptor)
    implementation(Deps.kotlinSerializationRuntime)
    implementation(Deps.kotlinCoroutinesAndroid)
    debugImplementation("com.facebook.flipper:flipper-network-plugin:0.66.0")
    debugImplementation("com.facebook.flipper:flipper:0.66.0")


    kapt(Deps.glideCompiler)
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