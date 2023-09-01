// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.sonarqube.gradle.plugin)
    }
}

allprojects {
    apply {
        plugin("org.sonarqube")
    }
    apply("${project.rootDir}/app/sonar.gradle")
}

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.org.jetbrains.kotlin.kapt) apply false
}
true // Needed to make the Suppress annotation work for the plugins block