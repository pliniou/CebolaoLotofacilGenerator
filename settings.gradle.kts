pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    
    // Configuração para evitar avisos de mutação de dependências
    // Usando o catálogo de versões já definido pelo Gradle, não é necessário criar outro
}

rootProject.name = "CebolaoLotofacilGenerator"

include(":app")