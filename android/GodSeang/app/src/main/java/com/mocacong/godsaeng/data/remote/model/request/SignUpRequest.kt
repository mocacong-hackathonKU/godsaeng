package com.mocacong.godsaeng.data.remote.model.request

data class SignUpRequest(
    val email: String,
    val nickname: String,
    val platform: String,
    val platformId: String
)