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
    implementation(project(":core-formatter"))
    implementation(project(":core-navigation"))

    implementation(project(":feature-list-shared"))

    Dependencies.lifecycle.apply {
        implementation(runtimeKtx)
        implementation(viewModelKtx)
        implementation(viewModelSavedstate)
    }

    Dependencies.di.apply {
        implementation(android)
        kapt(androidCompiler)
        kapt(hiltCompiler)
    }

    Dependencies.other.apply {
        implementation(recyclerView)
        implementation(timber)
        implementation(coil)
        implementation(fragmentKtx)
    }
}