plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = Android.compileSdk

    defaultConfig {
        minSdk = Android.minSdk
        targetSdk = Android.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    kapt {
        useBuildCache = true
        arguments {
            arg("dagger.fastInit", "enabled")
            arg("dagger.formatGeneratedSource", "disabled")
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core-common"))
    implementation(project(":core-model"))
    implementation(project(":core-data"))
    implementation(project(":core-ui"))

    implementation(project(":feature-details-chart"))
    implementation(project(":feature-details-summary"))
    implementation(project(":feature-details-news"))
    implementation(project(":feature-details-recommendations"))

    implementation(Dependencies.lifecycle.runtimeKtx)
    implementation(Dependencies.lifecycle.viewModelKtx)
    implementation(Dependencies.lifecycle.viewModelSavedstate)

    Dependencies.coroutines.apply {
        implementation(core)
        implementation(android)
    }

    Dependencies.di.apply {
        implementation(android)
        kapt(androidCompiler)
        kapt(hiltCompiler)
    }

    implementation(Dependencies.other.timber)
    implementation(Dependencies.other.fragmentKtx)
}