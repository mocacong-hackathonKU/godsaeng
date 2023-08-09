package com.mocacong.godsaeng.widget.extention

import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat

object ContextExtention {

    //object? companion? 나중에 보기

    fun EditText.handleEnterKey() {
        val inputMethodManager =
            ContextCompat.getSystemService(context, InputMethodManager::class.java)
        this.setOnKeyListener { _, code, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (code == KeyEvent.KEYCODE_ENTER)) {
                (inputMethodManager)?.hideSoftInputFromWindow(
                    this.windowToken,
                    0
                )
                true
            }
            false
        }
    }


}