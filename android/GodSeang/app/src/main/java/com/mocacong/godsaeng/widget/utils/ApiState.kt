package com.mocacong.godsaeng.widget.utils

import com.mocacong.godsaeng.data.remote.model.response.ErrorResponse

sealed class ApiState<T>(
    val data: T? = null,
    val message: String? = null,
    val errorResponse: ErrorResponse? = null
) {
    class Success<T>(data: T?) : ApiState<T>(data)
    class Error<T>(errorResponse: ErrorResponse, data: T? = null) :
        ApiState<T>(data, null, errorResponse)

    class Loading<T> : ApiState<T>()
}