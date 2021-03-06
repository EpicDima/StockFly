package dependencies

import Versions

object Hilt {

    const val android = "com.google.dagger:hilt-android:${Versions.hilt}"

    const val androidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"

    const val hiltCompiler = "androidx.hilt:hilt-compiler:${Versions.hiltCompiler}"

    const val workManager = "androidx.hilt:hilt-work:${Versions.hiltWorkManager}"
}