package com.mocacong.godsaeng.widget.utils

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "url"

    //interceptor 생성
    private val interceptorClient = OkHttpClient().newBuilder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(ResponseInterceptor())
        .build()

    // Retrofit 객체 생성
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(interceptorClient)
        .addConverterFactory(GsonConverterFactory.create(
            GsonBuilder()
                .setLenient()
                .create()))
        .build()


    // API 인터페이스 반환
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}

class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        return if (response.isSuccessful) response
        else {
            val errorJson = JSONObject(response.peekBody(2048).string())
            val code = errorJson.getInt("code")
            val message = errorJson.getString("message")
            Log.d("interceptor", "Network Error 발생! code = $code, message = $message")

            response
        }
    }

}