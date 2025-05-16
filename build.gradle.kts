// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}

// NOTA: O erro "Could not find androidx.room:room-ktx:2.6.4" precisa ser investigado
// no arquivo app/build.gradle (verifique a versão da dependência) e no arquivo
// settings.gradle (verifique se o repositório google() está listado em dependencyResolutionManagement).