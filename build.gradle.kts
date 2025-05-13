// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("com.android.application") version "8.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    // Não adicione id("com.google.devtools.ksp") aqui!
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}

// NOTA: O erro "Could not find androidx.room:room-ktx:2.6.4" precisa ser investigado
// no arquivo app/build.gradle (verifique a versão da dependência) e no arquivo
// settings.gradle (verifique se o repositório google() está listado em dependencyResolutionManagement).