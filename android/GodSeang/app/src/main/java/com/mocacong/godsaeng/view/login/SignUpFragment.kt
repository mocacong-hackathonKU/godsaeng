package com.mocacong.godsaeng.view.login

import BaseFragment
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mocacong.godsaeng.R
import com.mocacong.godsaeng.data.MemberInfo
import com.mocacong.godsaeng.data.remote.model.request.SignUpRequest
import com.mocacong.godsaeng.databinding.FragmentSignUpBinding
import com.mocacong.godsaeng.viewmodel.LogInViewModel
import com.mocacong.godsaeng.widget.extention.ContextExtention.handleEnterKey
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class SignUpFragment : BaseFragment<FragmentSignUpBinding>(R.layout.fragment_sign_up) {

    private lateinit var body: MultipartBody.Part
    private val viewModel: LogInViewModel by activityViewModels()
    private val nicknameErrorText = "닉네임은 한글과 영어 2~6글자로 작성해주세요"


    override fun afterViewCreated() {
        binding.profileImageBtn.setOnClickListener {
            getProfileImg()
        }
        binding.nicknameEditText.apply {
            handleEnterKey()
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.nicknameLayout.error =
                        if (nicknameRegex(p0.toString())) null
                        else nicknameErrorText
                }
            })
        }
        binding.cancelButton.setOnClickListener {
            startNextActivity(LogInActivity::class.java)
        }
        binding.completeButton.setOnClickListener {
            if (!binding.nicknameLayout.error.isNullOrBlank()) {
                showToast("닉네임 형식을 확인해주세요")
                return@setOnClickListener
            }
            registerMember()
        }
    }

    private fun registerMember() {
        lifecycleScope.launch {
            val nickname = binding.nicknameEditText.text.toString()
            if (isDuplicated(nickname) == true) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("중복된 닉네임")
                    .setMessage("중복된 닉네임입니다. 다시 입력해주세요.")
                    .setPositiveButton("확인", null)
                    .show()
                return@launch
            } else {
                //postSignup
                val info = SignUpRequest(
                    email = MemberInfo.data.email,
                    nickname = nickname,
                    platform = "kakao",
                    platformId = MemberInfo.data.platformId
                )
                viewModel.requestKakaoSignUp(info).join()
                consumeResponse(viewModel.signUpFlow.value,
                    onSuccess = {
                        (requireActivity() as LogInActivity).kakaoLogin()
                        return@consumeResponse it
                    },
                    onError = {
                        showToast(it.message)
                    }
                )
            }
        }
    }

    private fun nicknameRegex(nickname: String): Boolean {
        val pattern = "^[a-zA-Zㄱ-힣]{2,6}\$".toRegex()
        return nickname.matches(pattern)
    }


    private suspend fun isDuplicated(nickname: String) =
        lifecycleScope.async {
            viewModel.requestNicknameCheck(nickname).join()
            return@async consumeResponse(viewModel.checkFlow.value,
                onSuccess = {
                    return@consumeResponse it
                },
                onError = {
                    showToast(it.message)
                }
            )
        }.await()


    private fun getProfileImg() {
        val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        ProfileImageLauncher.launch(pickImg)
    }

    private val ProfileImageLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                binding.profileImageBtn.setImageURI(imgUri)
                val file = File(absolutePath(imgUri))
                val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                Log.d("login", "body = $body")
            }
        }

    private fun absolutePath(uri: Uri?): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = requireActivity().contentResolver.query(uri!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()
        val result = c?.getString(index!!)
        c?.close()
        return result!!
    }

}