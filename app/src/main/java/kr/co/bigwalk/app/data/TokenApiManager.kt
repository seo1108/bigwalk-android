package kr.co.bigwalk.app.data

import com.google.gson.GsonBuilder
import kr.co.bigwalk.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TokenApiManager {

    fun getService(): TokenApiService = retrofit.create(
        TokenApiService::class.java
    )

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.URL_API_SERVER)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create()
            )
        )
        .client(okHttpClient.build())
        .build()

    fun getErrorResponse(errorBody: ResponseBody): ErrorResponse {
        retrofit.responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java, ErrorResponse::class.java.annotations
        ).convert(errorBody)?.let {
            return it
        }
        return ErrorResponse(0, "알 수 없는 에러")
    }
}