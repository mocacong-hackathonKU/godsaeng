package com.mocacong.godsaeng.view.login

import BaseActivity
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mocacong.godsaeng.BuildConfig
import com.mocacong.godsaeng.R
import com.mocacong.godsaeng.data.MemberInfo
import com.mocacong.godsaeng.data.remote.model.response.LogInResponse
import com.mocacong.godsaeng.databinding.ActivityLogInBinding
import com.mocacong.godsaeng.repository.LogInRepository
import com.mocacong.godsaeng.view.main.MainActivity
import com.mocacong.godsaeng.viewmodel.LogInViewModel
import com.mocacong.godsaeng.widget.utils.ViewModelFactory
import kotlinx.coroutines.launch

class LogInActivity : BaseActivity<ActivityLogInBinding, LogInViewModel>(R.layout.activity_log_in) {
    private val TAG = "Login"

    override val viewModel: LogInViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(LogInRepository()))[LogInViewModel::class.java]
    }

    private lateinit var authCode: String

    private val REST_API_KEY: String = BuildConfig.KAKAO_REST_API_KEY
    private val REDIRECT_URI: String = BuildConfig.KAKAO_REDIRECT_URI


    override fun initView() {
        val loadingObserver = Observer<Boolean> {
            binding.progressCircular.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.isLoading.observe(this, loadingObserver)
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
                isLoading.value = true
                requestKakaoLogin(authCode).join()
                consumeResponse(apiState = loginFlow.value,
                    onSuccess = {
                        isLoading.value = false
                        Log.d(TAG, "kakaoLogin() isRegistered = ${it.isRegistered}")
                        if (it.isRegistered) loginSuccessed(it)
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

    private fun loginSuccessed(member: LogInResponse) {
        Log.d(TAG, "Login Succeed. Member : $member")
        MemberInfo.data = member
        showToast("로그인 성공. 환영합니다")
        startNextActivity(MainActivity::class.java)
    }

    private fun registerMember(member: LogInResponse) {
        MemberInfo.data = member
        showToast("회원 정보 없음. 가입을 시작합니다")
        supportFragmentManager
            .findFragmentByTag("SignUpFragment")
            ?: SignUpFragment().also { fragment ->
                addFragment(fragment)
            }
    }

    private fun addFragment(fragment: SignUpFragment) {
        supportFragmentManager.beginTransaction().add(R.id.login_frame, fragment, "SignUpFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commitNow()
    }
}