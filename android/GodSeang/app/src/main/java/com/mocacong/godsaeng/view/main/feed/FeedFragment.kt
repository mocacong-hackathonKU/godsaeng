package com.mocacong.godsaeng.view.main.feed

import BaseFragment
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.mocacong.godsaeng.R
import com.mocacong.godsaeng.data.CalendarEntity
import com.mocacong.godsaeng.data.DailyInfo
import com.mocacong.godsaeng.data.GodSaeng
import com.mocacong.godsaeng.databinding.FragmentFeedBinding
import java.util.*

class FeedFragment : BaseFragment<FragmentFeedBinding>(R.layout.fragment_feed) {

    private var currentYear: Int = 0
    private var currentMonth: Int = 0
    private val dailyAdapter = DailyGodSaengAdapter()
    override fun afterViewCreated() {
        setCalendarView()
        setListeners()
        setGodSaengList()
    }

    private fun setGodSaengList() {
        dailyAdapter.setOnItemClickListener(object : DailyGodSaengAdapter.OnItemClickListener {
            override fun onItemClick(item: DailyInfo) {
                Log.d("Feed", "Daily Item : $item 선택됨")
                //TODO: 상세페이지로 넘어가기
            }
        })

        dailyAdapter.setData(
            listOf(
                DailyInfo(id = 12124141, "같생111", GodSaeng.STATUS.PROCEEDING.name),
                DailyInfo(id = 12222222, "같생222", GodSaeng.STATUS.PROCEEDING.name),
                DailyInfo(id = 13333333, "같생1113", GodSaeng.STATUS.DONE.name),
                DailyInfo(id = 12444441, "같생1114", GodSaeng.STATUS.DONE.name),
            )
        )

        binding.godSaengsRecyclerView.apply {
            adapter = dailyAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setListeners() {
        binding.apply {
            beforeMonth.setOnClickListener {
                if (currentMonth == 1) {
                    currentYear--
                    currentMonth = 13
                }
                calendarView.initialize(
                    currentYear,
                    --currentMonth,
                    generateMonthlyInfo(currentYear, currentMonth)
                )
            }
            nextMonth.setOnClickListener {
                if (currentMonth == 12) {
                    currentYear--
                    currentMonth = 0
                }
                calendarView.initialize(
                    currentYear,
                    ++currentMonth,
                    generateMonthlyInfo(currentYear, currentMonth)
                )
            }
        }
    }


    private fun setCalendarView() {
        val calendar = Calendar.getInstance()

        currentYear = calendar.get(Calendar.YEAR)
        currentMonth = calendar.get(Calendar.MONTH) + 1
        //나중에 remote로부터 get
        val monthlyInfo = generateMonthlyInfo(currentYear, currentMonth)
        binding.calendarView.initialize(currentYear, currentMonth, monthlyInfo)
        binding.calendarView.setDateClickListener(object : CalendarViewAdapter.OnItemClickListener {
            override fun onItemClick(item: CalendarEntity) {
                Log.d("Calendar", "${item.date}일 선택됨")
                //getDailyInfo
            }
        })
    }

    private fun generateMonthlyInfo(year: Int, month: Int): List<CalendarEntity> {
        val calendarEntities = mutableListOf<CalendarEntity>()

        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1) // 월은 0부터 시작하므로 -1

        val maxDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (day in 1..maxDayInMonth) {
            // Here, just for demonstration, I'm using a random choice for the status.
            // In real scenarios, you might get this data from a server or database.
            val status = listOf("PROCEEDING", "DONE", "").random()
            val date = String.format("%d-%02d-%02d", year, month, day)
            calendarEntities.add(CalendarEntity(date, status))
        }
        return calendarEntities
    }
}