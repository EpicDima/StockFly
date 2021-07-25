package dependencies

import Versions

object Retrofit {

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"

    const val moshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"

    const val interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
}