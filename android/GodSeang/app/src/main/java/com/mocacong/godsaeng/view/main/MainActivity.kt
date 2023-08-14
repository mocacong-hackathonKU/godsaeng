package com.mocacong.godsaeng.view.main

import BaseActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.mocacong.godsaeng.R
import com.mocacong.godsaeng.databinding.ActivityMainBinding
import com.mocacong.godsaeng.repository.MainRepository
import com.mocacong.godsaeng.view.main.feed.FeedFragment
import com.mocacong.godsaeng.view.main.search.SearchFragment
import com.mocacong.godsaeng.viewmodel.MainViewModel
import com.mocacong.godsaeng.widget.utils.ViewModelFactory

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(MainRepository()))[MainViewModel::class.java]
    }

    override fun initView() {
    }

    override fun initListener() {
        binding.btnv.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_feed -> {
                    replaceFragment(FeedFragment())
                    true
                }
                R.id.menu_search -> {
                    replaceFragment(SearchFragment())
                    true
                }
                R.id.menu_mypage -> {

                    true
                }
                else -> {
                    false
                }
            }
        }
        binding.btnv.selectedItemId = R.id.menu_feed
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment, fragment.tag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commitNow()
    }

}