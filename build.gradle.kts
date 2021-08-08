buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.39.0")
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val nonStableKeyword =
        listOf("AlPHA", "BETA", "PREVIEW", "M", "RC").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = (stableKeyword || regex.matches(version)) && !nonStableKeyword
    return isStable.not()
}

allprojects {
    apply {
        plugin("com.github.ben-manes.versions")
    }

    tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates")
        .configure {
            gradleReleaseChannel = "current"
        }

    tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}