// Plugins are resolved from the version catalog and applied per-module
// (see app/build.gradle.kts). The `plugins {}` block with `apply false` is what
// puts them on the classpath for subprojects; a buildscript classpath plus
// repositories here would duplicate that.
plugins {
    id("com.android.application") version libs.versions.androidGradlePlugin.get() apply false
    id("org.jetbrains.kotlin.android") version libs.versions.kotlin.get() apply false
    id("org.jetbrains.kotlin.plugin.compose") version libs.versions.kotlin.get() apply false
    id("org.jetbrains.kotlin.plugin.serialization") version libs.versions.kotlin.get() apply false
    id("com.google.dagger.hilt.android") version libs.versions.hilt.get() apply false
    id("com.google.devtools.ksp") version libs.versions.ksp.get() apply false
}

// No allprojects { repositories { } } block on purpose. settings.gradle.kts sets
// RepositoriesMode.FAIL_ON_PROJECT_REPOS, so declaring repositories in a build
// script aborts the build with "repository 'Google' was added by build file".

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
