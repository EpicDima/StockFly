import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = Android.compileSdk

    defaultConfig {
        applicationId = "com.epicdima.stockfly"
        minSdk = Android.minSdk
        targetSdk = Android.targetSdk
        versionCode = 4
        versionName = "0.3.1"

        buildConfigField(
            "String",
            "API_KEY",
            gradleLocalProperties(rootDir).getProperty("API_KEY").toString()
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()

        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
    kapt {
        useBuildCache = true
        arguments {
            arg("dagger.fastInit", "enabled")
            arg("dagger.formatGeneratedSource", "disabled")
        }
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
            versionNameSuffix = ".$name"
        }
    }
}

dependencies {
    implementation(project(":core-buildconfig"))
    implementation(project(":core-common"))
    implementation(project(":core-utils"))
    implementation(project(":core-navigation"))
    implementation(project(":core-ui"))
    implementation(project(":core-model"))
    implementation(project(":core-customtabs"))
    implementation(project(":core-shortcuts"))
    implementation(project(":core-work"))
    implementation(project(":core-formatter"))

    implementation(project(":feature-list"))
    implementation(project(":feature-list-search"))
    implementation(project(":feature-details"))

    Dependencies.main.apply {
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
        kapt(hiltCompiler)
        implementation(workManager)
    }

    Dependencies.other.apply {
        implementation(fragmentKtx)
        implementation(coil)
        implementation(timber)
    }

    Dependencies.debug.apply {
        debugImplementation(leakCanary)
    }
}