// Top-level build file where you can add configuration options common to all sub-projects/modules.

val gradleVersion = "8.3.0" // Atualizado de 8.0.0 para melhor compatibilidade com compileSdk 34
val kotlinVersion = "1.9.23" // Corrigido para coincidir com libs.versions.toml

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$gradleVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("com.android.application") version "8.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// NOTA: O erro "Could not find androidx.room:room-ktx:2.6.4" precisa ser investigado
// no arquivo app/build.gradle (verifique a versão da dependência) e no arquivo
// settings.gradle (verifique se o repositório google() está listado em dependencyResolutionManagement).