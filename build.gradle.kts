// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
        maven("https://maven.fabric.io/public")
    }
    dependencies {
        classpath(Deps.androidGradlePlugin)
        classpath(kotlin("gradle-plugin", version = Deps.kotlinVersion))
        classpath("io.realm:realm-gradle-plugin:5.8.0")
        classpath("com.deploygate:gradle:1.1.4")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.17.0")
        classpath("com.cookpad.android.licensetools:license-tools-plugin:1.5.0")
        classpath("com.google.gms:google-services:4.2.0")
        classpath("io.fabric.tools:gradle:1.26.1")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.3.1.1")
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://maven.google.com/")
        maven("http://dl.bintray.com/jetbrains/spek")
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}

//  Use user-defined values in local.properties as ${rootProject.ext["KEY"]}"
loadProperties("$rootDir/local.properties", ext)