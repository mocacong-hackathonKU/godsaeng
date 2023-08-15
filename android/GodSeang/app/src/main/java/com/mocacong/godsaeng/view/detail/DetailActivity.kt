package com.mocacong.godsaeng.view.detail

import BaseActivity
import androidx.lifecycle.ViewModelProvider
import com.mocacong.godsaeng.R
import com.mocacong.godsaeng.databinding.ActivityDetailBinding
import com.mocacong.godsaeng.repository.DetailRepository
import com.mocacong.godsaeng.viewmodel.DetailViewModel
import com.mocacong.godsaeng.widget.utils.ViewModelFactory

class DetailActivity :
    BaseActivity<ActivityDetailBinding, DetailViewModel>(R.layout.activity_detail) {
    override val viewModel: DetailViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(DetailRepository()))[DetailViewModel::class.java]
    }

    override fun initView() {
        binding.viewModel = viewModel
        viewModel.fetchDetailData()
        setWeeklyView()
    }

    private fun setWeeklyView() {
        viewModel.detailData.value?.let { binding.weekSelector.selectDays(it.weeks) }
        binding.weekSelector.isClickable = false
    }

    override fun initListener() {

    }


}