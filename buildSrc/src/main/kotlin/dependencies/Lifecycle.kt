package dependencies

import Versions

object Lifecycle {

    const val common = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"

    const val extensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleExt}"

    const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"

    const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"

    const val viewModelSavedstate = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}"

    const val archExt = "android.arch.lifecycle:extensions:${Versions.archLfecycleExt}"
}