// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
        maven("https://maven.fabric.io/public")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.0-beta05")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.21")
        classpath("io.realm:realm-gradle-plugin:5.8.0")
        classpath("com.deploygate:gradle:1.1.4")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.17.0")
        classpath("com.cookpad.android.licensetools:license-tools-plugin:1.5.0")
        classpath("com.google.gms:google-services:4.2.0")
        classpath("io.fabric.tools:gradle:1.26.1")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.3.1.1")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://maven.google.com/")
    }
}

task("clean",  Delete::class) {
    delete = setOf(rootProject.buildDir)
}