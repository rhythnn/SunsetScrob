const val kotlinVersion = "1.3.72"

object Deps {
    private const val ktorVersion = "1.2.2"

    const val appCompat = "androidx.appcompat:appcompat:1.1.0"
    const val androidGradlePlugin = "com.android.tools.build:gradle:4.2.0-alpha04"
    const val browser = "androidx.browser:browser:1.0.0"
    const val cardView = "androidx.cardview:cardview:1.0.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
    const val crashlytics = "com.crashlytics.sdk.android:crashlytics:2.10.1"

    const val epoxy = "com.airbnb.android:epoxy:3.9.0"
    const val epoxyDatabinding = "com.airbnb.android:epoxy-databinding:3.9.0"
    const val epoxyProcessor = "com.airbnb.android:epoxy-processor:3.9.0"

    const val firebaseCore = "com.google.firebase:firebase-core:17.0.0"

    const val glide = "com.github.bumptech.glide:glide:4.11.0"
    const val glideCompiler = "com.github.bumptech.glide:compiler:4.11.0"

    const val gradleVersionsPlugin = "com.github.ben-manes:gradle-versions-plugin:0.27.0"

    const val koinAndroid = "org.koin:koin-android:2.0.1"
    const val koinAndroidXScope = "org.koin:koin-androidx-scope:2.0.1"
    const val koinAndroidXViewModel = "org.koin:koin-androidx-viewmodel:2.0.1"

    const val kotlinCoroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.3"
    const val kotlinSerializationRuntime =
        "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0"
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"

    const val ktorClientOkhttp = "io.ktor:ktor-client-okhttp:$ktorVersion"
    const val ktorClientJsonJvm = "io.ktor:ktor-client-serialization-jvm:$ktorVersion"

    const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.0.0"
    const val materialComponent = "com.google.android.material:material:1.0.0"

    const val okhttp = "com.squareup.okhttp3:okhttp:3.14.2"
    const val okhttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:3.14.2"

    const val preference = "androidx.preference:preference:1.0.0"

    const val spek = "org.jetbrains.spek:spek-api:1.1.5"
    const val spekJunitPlatformEngine = "org.jetbrains.spek:spek-junit-platform-engine:1.1.5"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
    const val junitPlatformRunner = "org.junit.platform:junit-platform-runner:1.1.0"

    const val roomRuntime = "androidx.room:room-runtime:2.0.0"
    const val roomCompiler = "androidx.room:room-compiler:2.0.0"
}

object UnitTestDeps {
    const val androidxTestCore = "androidx.test:core:1.1.0"
    const val spek = "org.jetbrains.spek:spek-api:1.1.5"
    const val spekJunitPlatformEngine = "org.jetbrains.spek:spek-junit-platform-engine:1.1.5"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
    const val junitPlatformRunner = "org.junit.platform:junit-platform-runner:1.1.0"
    const val kotlinTestJunit = "org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion"
    const val robolectric = "org.robolectric:robolectric:4.0.2"
    const val guava = "com.google.guava:guava:22.0"
    const val junit = "junit:junit:4.12"
    const val mockWebServer = "com.squareup.okhttp3:mockwebserver:3.12.0"
    const val mockito = "org.mockito:mockito-core:2.27.0"
}
