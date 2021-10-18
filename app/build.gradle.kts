import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = Android.compileSdk
    buildToolsVersion = Android.buildTools

    defaultConfig {
        applicationId = "com.epicdima.stockfly"
        minSdk = Android.minSdk
        targetSdk = Android.targetSdk
        versionCode = 3
        versionName = "0.3"

        vectorDrawables.useSupportLibrary = true

        buildConfigField(
            "String",
            "API_KEY",
            gradleLocalProperties(rootDir).getProperty("API_KEY").toString()
        )

        kapt {
            useBuildCache = true
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("dagger.fastInit", "enabled")
                arg("dagger.formatGeneratedSource", "disabled")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    flavorDimensions.addAll(listOf("search"))
    productFlavors {
        create("detailed") {
            dimension = "search"
            buildConfigField("boolean", "DETAILED_SEARCH", "true")
        }
        create("undetailed") {
            dimension = "search"
            buildConfigField("boolean", "DETAILED_SEARCH", "false")
        }
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    Dependencies.main.apply {
        implementation(kotlinStd)
        implementation(coreKtx)
        implementation(appCompat)
    }

    Dependencies.coroutines.apply {
        implementation(core)
        implementation(android)
    }

    Dependencies.di.apply {
        implementation(android)
        kapt(androidCompiler)
    }

    Dependencies.lifecycle.apply {
        implementation(runtimeKtx)
        implementation(viewModelKtx)
        implementation(viewModelSavedstate)
        implementation(archExt)
    }

    Dependencies.room.apply {
        implementation(runtime)
        kapt(compiler)
        implementation(ktx)
    }

    Dependencies.retrofit.apply {
        implementation(retrofit)
        implementation(moshiConverter)
        implementation(interceptor)
    }

    Dependencies.moshi.apply {
        implementation(moshi)
        kapt(codegen)
    }

    Dependencies.other.apply {
        implementation(material)
        implementation(constraint)
        implementation(recyclerView)
        implementation(fragmentKtx)
        implementation(coil)
        implementation(timber)
        implementation(asynclayoutinflater)
        implementation(browser)
    }

    Dependencies.debug.apply {
        debugImplementation(leakCanary)
    }
}