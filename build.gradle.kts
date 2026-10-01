buildscript {
    dependencies {
        val agpVersion = libs.versions.androidGradlePlugin.get()
        val kotlinVersion = libs.versions.kotlin.get()
        add("classpath", "com.android.tools.build:gradle:$agpVersion")
        add("classpath", "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        add("classpath", "com.google.gms:google-services:4.4.2")
    }
}

plugins {
    id("com.android.application") version libs.versions.androidGradlePlugin.get() apply false
    id("org.jetbrains.kotlin.android") version libs.versions.kotlin.get() apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}