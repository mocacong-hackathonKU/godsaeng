package com.mocacong.godsaeng.data.remote.model.response

data class LogInResponse(
    val token : String,
    val email : String,
    val isRegistered : Boolean,
    val platformId : String
)
