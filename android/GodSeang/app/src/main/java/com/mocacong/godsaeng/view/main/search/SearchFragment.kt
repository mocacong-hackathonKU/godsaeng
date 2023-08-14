package com.mocacong.godsaeng.view.main.search

import BaseFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mocacong.godsaeng.R
import com.mocacong.godsaeng.data.Preview
import com.mocacong.godsaeng.databinding.FragmentSearchBinding

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    val adapter = GodSaengListAdapter()
    override fun afterViewCreated() {
        val godSaengs = getGodSaengList()
        setRecyclerView(godSaengs)
    }

    private fun setRecyclerView(godSaengs: List<Preview>) {
        adapter.setData(godSaengs)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }

    private fun getGodSaengList(): List<Preview> {
        //TODO: remote단 GET으로 변경

        return listOf(
            Preview(1233331, "같생제목1", "아침 9시에 일어나기", listOf("MON", "WED", "THU")),
            Preview(1233332, "같생제목2", "점심 12시에 밥 먹기", listOf("TUE", "THU", "FRI")),
            Preview(1233333, "같생제목3", "저녁 6시에 운동하기", listOf("MON", "TUE")),
            Preview(1233334, "같생제목4", "저녁 7시에 책 읽기", listOf("WED", "FRI", "SUN")),
            Preview(1233335, "같생제목5", "아침 8시에 명상하기", listOf("MON", "TUE", "WED", "THU")),
            Preview(1233336, "같생제목6", "밤 10시에 잠자기", listOf("FRI", "SAT")),
            Preview(1233337, "같생제목7", "아침 7시에 조깅하기", listOf("SAT", "SUN"))
        )
    }
}