package kr.co.bigwalk.app.data

import com.google.gson.GsonBuilder
import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.data.api.BlameApi
import kr.co.bigwalk.app.data.api.ReportApi
import kr.co.bigwalk.app.data.api.UserApi
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type


object RemoteApiManager {

    fun getService(): RemoteApiService = retrofit.create(
        RemoteApiService::class.java)

    fun getBlameApi(): BlameApi = retrofit.create(
        BlameApi::class.java)

    fun getUserApi(): UserApi = retrofit.create(
        UserApi::class.java)

    fun getReportApi(): ReportApi = retrofit.create(
        ReportApi::class.java)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.URL_API_SERVER)
        //.addConverterFactory(nullOnEmptyConverterFactory)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()))
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

    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
            override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) {
                try{
                    nextResponseBodyConverter.convert(value)
                }catch (e:Exception){
                    e.printStackTrace()
                    null
                }
            } else{
                null
            }
        }
    }
}