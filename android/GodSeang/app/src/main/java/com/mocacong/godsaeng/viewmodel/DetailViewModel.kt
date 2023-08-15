package com.mocacong.godsaeng.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mocacong.godsaeng.data.Detail
import com.mocacong.godsaeng.data.GodSaeng
import com.mocacong.godsaeng.data.Member
import com.mocacong.godsaeng.repository.DetailRepository

class DetailViewModel(val detailRepository: DetailRepository) : ViewModel() {

    var _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading

    var isJoinable = MutableLiveData<Boolean>()

    private var _detailData = MutableLiveData<Detail>()
    val detailData: LiveData<Detail> get() = _detailData

    fun fetchDetailData(): Detail {

        val data = Detail(
            id = 12345678,
            title = "같생제목임",
            description = "우리같생은짱이에요",
            weeks = listOf("MON", "TUE"),
            openDate = "2023-08-16",
            closeDate = "2023-08-23",
            members = listOf(Member("디듀", ""), Member("디듀2", "")),
            progress = 34,
            status = GodSaeng.STATUS.PROCEEDING.name,
            proofs = listOf()
        )
        _detailData.value = data
        return data

    }

}