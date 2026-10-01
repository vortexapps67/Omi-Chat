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
    // No explicit versionCatalogs block: Gradle auto-creates the `libs` catalog
    // from gradle/libs.versions.toml, which is where the file already lives.
    // Declaring it here as well makes Gradle call `from` twice on the same
    // catalog and abort configuration with "you can only call the 'from' method
    // a single time".
}

rootProject.name = "OmiChat"

include(":app")
