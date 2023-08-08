package com.mocacong.godsaeng.widget.utils

import com.mocacong.godsaeng.data.remote.model.response.ErrorResponse
import okhttp3.ResponseBody

fun getErrorResponse(errorBody: ResponseBody): ErrorResponse? {
    return RetrofitClient.retrofit.responseBodyConverter<ErrorResponse>(
        ErrorResponse::class.java,
        ErrorResponse::class.java.annotations
    ).convert(errorBody)
}