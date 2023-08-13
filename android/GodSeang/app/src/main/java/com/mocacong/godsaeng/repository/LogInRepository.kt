package com.mocacong.godsaeng.repository

import com.mocacong.godsaeng.data.remote.api.LogInApi
import com.mocacong.godsaeng.data.remote.model.request.SignUpRequest
import com.mocacong.godsaeng.data.remote.model.response.LogInResponse
import com.mocacong.godsaeng.data.remote.model.response.NickNameDuplicateResponse
import com.mocacong.godsaeng.widget.utils.ApiState
import com.mocacong.godsaeng.widget.utils.NetworkUtil
import com.mocacong.godsaeng.widget.utils.RetrofitClient

class LogInRepository {
    private val api = RetrofitClient.create(LogInApi::class.java)

    suspend fun postLogin(code: String?): ApiState<LogInResponse> {
        val response = api.login(code)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    suspend fun postSignUp(info: SignUpRequest): ApiState<Void> {
        val response = api.signUp(info)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    suspend fun checkDuplicate(nickname: String): ApiState<NickNameDuplicateResponse> {
        val response = api.getDuplicated(nickname)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }
}