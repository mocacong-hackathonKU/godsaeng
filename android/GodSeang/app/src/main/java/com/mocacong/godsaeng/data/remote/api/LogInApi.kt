package com.mocacong.godsaeng.data.remote.api

import com.mocacong.godsaeng.data.remote.model.request.SignUpRequest
import com.mocacong.godsaeng.data.remote.model.response.LogInResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LogInApi {
    @POST("login/kakao")
    suspend fun login(@Body code: String?): Response<LogInResponse>

    @POST("members/oauth")
    suspend fun signUp(@Body info: SignUpRequest): Response<Void>

    @GET("members/check-duplicate/nickname")
    suspend fun getDuplicated(@Query("value") value: String): Response<Boolean>
}