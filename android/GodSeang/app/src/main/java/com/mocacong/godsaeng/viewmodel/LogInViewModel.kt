package com.mocacong.godsaeng.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocacong.godsaeng.data.remote.model.request.SignUpRequest
import com.mocacong.godsaeng.data.remote.model.response.LogInResponse
import com.mocacong.godsaeng.data.remote.model.response.NickNameDuplicateResponse
import com.mocacong.godsaeng.repository.LogInRepository
import com.mocacong.godsaeng.widget.utils.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LogInViewModel(val logInRepository: LogInRepository) : ViewModel() {
    var mLoginFlow: MutableStateFlow<ApiState<LogInResponse>> = MutableStateFlow(ApiState.Loading())
    var loginFlow: StateFlow<ApiState<LogInResponse>> = mLoginFlow

    var mSignUpFlow: MutableStateFlow<ApiState<Void>> = MutableStateFlow(ApiState.Loading())
    var signUpFlow: StateFlow<ApiState<Void>> = mSignUpFlow

    var mCheckFlow: MutableStateFlow<ApiState<NickNameDuplicateResponse>> = MutableStateFlow(ApiState.Loading())
    var checkFlow: StateFlow<ApiState<NickNameDuplicateResponse>> = mCheckFlow


    fun requestKakaoLogin(code: String?) = viewModelScope.launch(Dispatchers.IO) {
        mLoginFlow.value = ApiState.Loading()
        mLoginFlow.value = logInRepository.postLogin(code)
    }

    fun requestKakaoSignUp(info: SignUpRequest) = viewModelScope.launch(Dispatchers.IO) {
        mSignUpFlow.value = ApiState.Loading()
        mSignUpFlow.value = logInRepository.postSignUp(info)
    }

    fun requestNicknameCheck(nickname: String) = viewModelScope.launch(Dispatchers.IO) {
        mCheckFlow.value = ApiState.Loading()
        mCheckFlow.value = logInRepository.checkDuplicate(nickname)
    }
}