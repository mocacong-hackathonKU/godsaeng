package com.mocacong.godsaeng.view.login

import BaseActivity
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mocacong.godsaeng.MainActivity
import com.mocacong.godsaeng.R
import com.mocacong.godsaeng.data.MemberInfo
import com.mocacong.godsaeng.data.remote.model.response.LogInResponse
import com.mocacong.godsaeng.databinding.ActivityLogInBinding
import com.mocacong.godsaeng.repository.LogInRepository
import com.mocacong.godsaeng.viewmodel.LogInViewModel
import com.mocacong.godsaeng.widget.utils.ViewModelFactory
import kotlinx.coroutines.launch

class LogInActivity : BaseActivity<ActivityLogInBinding, LogInViewModel>(R.layout.activity_log_in) {

    override val viewModel: LogInViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(LogInRepository()))[LogInViewModel::class.java]
    }

    private lateinit var authCode: String

    private val REST_API_KEY: String by lazy { getString(R.string.KAKAO_REST_API_KEY) }
    private val REDIRECT_URI: String by lazy { getString(R.string.KAKAO_REDIRECT_URI) }


    override fun initView() {
        MaterialAlertDialogBuilder(this)
            .setTitle("중복된 닉네임")
            .setMessage("중복된 닉네임입니다. 다시 입력해주세요.")
            .setPositiveButton("확인", null)
            .show()
    }

    override fun initListener() {
        binding.kakaoBtn.setOnClickListener {
            gotoKakaoOauth()
        }
    }

    private fun gotoKakaoOauth() {
        binding.loginFrame.visibility = View.VISIBLE

        val webView = binding.webView
        webView.visibility = View.VISIBLE

        val uri =
            "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}"

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (url?.startsWith(REDIRECT_URI) == true) {
                    authCode = Uri.parse(url).getQueryParameter("code").toString()
                    Log.d("kakaoOauth", "authcode : ${authCode}")
                    kakaoLogin()
                    webView.visibility = View.GONE
                }
                super.onPageStarted(view, url, favicon)
            }
        }
        webView.loadUrl(uri)
    }


    fun kakaoLogin() {
        lifecycleScope.launch {
            viewModel.apply {
                requestKakaoLogin(authCode).join()
                consumeResponse(apiState = loginFlow.value,
                    onSuccess = {
                        if(it.isRegistered) loginSuccessed(it)
                        else registerMember(it)
                        it
                    },
                    onError = {
                        showToast(it.message)
                    }
                )
            }
        }
    }

    private fun loginSuccessed(member: LogInResponse){
        MemberInfo.data = member
        showToast("로그인 성공. 환영합니다")
        startNextActivity(MainActivity::class.java)
    }

    private fun registerMember(member: LogInResponse){
        MemberInfo.data = member
        showToast("회원 정보 없음. 가입을 시작합니다")
         supportFragmentManager
            .findFragmentByTag("SignUpFragment")
            ?: SignUpFragment().also {fragment->
                addFragment(fragment)
            }
    }

    private fun addFragment(fragment: SignUpFragment){
        supportFragmentManager.beginTransaction().add(R.id.login_frame, fragment, "SignUpFragment").commit()
    }
}