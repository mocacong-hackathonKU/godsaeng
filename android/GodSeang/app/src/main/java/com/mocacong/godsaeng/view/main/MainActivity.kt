package com.mocacong.godsaeng.view.main

import BaseActivity
import androidx.lifecycle.ViewModelProvider
import com.mocacong.godsaeng.R
import com.mocacong.godsaeng.databinding.ActivityMainBinding
import com.mocacong.godsaeng.repository.MainRepository
import com.mocacong.godsaeng.viewmodel.MainViewModel
import com.mocacong.godsaeng.widget.utils.ViewModelFactory

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(MainRepository()))[MainViewModel::class.java]
    }



    override fun initView() {

    }

    override fun initListener() {
        TODO("Not yet implemented")
    }
}