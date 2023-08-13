package com.mocacong.godsaeng.view.login

import BaseFragment
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
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

    private var body: MultipartBody.Part? = null
    private val viewModel: LogInViewModel by activityViewModels()
    private val nicknameErrorText = "닉네임은 한글과 영어 2~6글자로 작성해주세요"


    override fun afterViewCreated() {
        binding.profileImageBtn.setOnClickListener {
            pickImage()
        }
        binding.nicknameEditText.apply {
            handleEnterKey()
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.toString() == "") binding.nicknameLayout.error = nicknameErrorText
                }

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
            if(binding.nicknameEditText.text.isNullOrBlank())
                showToast("닉네임을 입력해주세요")
            if (binding.nicknameLayout.error != null) {
                showToast("닉네임 형식을 확인해주세요")
            } else registerMember()
        }
    }

    private fun registerMember() {
        lifecycleScope.launch {
            val nickname = binding.nicknameEditText.text.toString()
            if (isDuplicated(nickname) == true) {
                //닉네임 중복체크
                showMessageDialog("중복된 닉네임", "중복된 닉네임입니다.")
                return@launch
            }

            if (body == null) {
                showMessageDialog("프로필 이미지", "프로필 이미지를 등록해주세요")
                return@launch
            }

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
            )?.result
        }.await()


    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 권한이 허용된 경우
                val pickImg =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                ProfileImageLauncher.launch(pickImg)
            } else {
                // 권한이 거부된 경우
                showToast("권한 거부됨")
            }
        }

    private fun pickImage() {
        when {
            checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 이미 권한이 허용된 경우
                val pickImg =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                ProfileImageLauncher.launch(pickImg)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                // 권한 요청에 대한 설명이 필요한 경우 (사용자가 이전에 거부한 적이 있는 경우)
                // 여기에서 사용자에게 왜 권한이 필요한지 설명하고, 다시 권한 요청을 수행
                showMessageDialog("권한", "프로필 사진 등록을 위해 권한이 필요합니다.")
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
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