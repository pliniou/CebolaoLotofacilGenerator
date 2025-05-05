pluginManagement {
    repositories {
        google()
        maven(url = "https://maven.google.com/")
        mavenCentral()
        gradlePluginPortal()
        maven {
            setUrl("https://maven.pkg.jetbrains.space/public/p/compose/dev")
            content { 
                includeGroup("org.jetbrains.compose")
                includeGroup("org.jetbrains.kotlinx")
            }
        }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
   }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Cebolao Lotofacil Generator"
include(":app")
