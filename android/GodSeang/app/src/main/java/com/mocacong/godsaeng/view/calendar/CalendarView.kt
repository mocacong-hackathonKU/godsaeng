package com.mocacong.godsaeng.view.calendar

import CalendarViewAdapter
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.mocacong.godsaeng.R
import com.mocacong.godsaeng.data.CalendarEntity
import com.mocacong.godsaeng.databinding.ViewCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var calendarViewBinding: ViewCalendarBinding
    private val calendar: Calendar = Calendar.getInstance()
    private val calendarEntities = mutableListOf<CalendarEntity>()
    private val adapter = CalendarViewAdapter()

    val selectedDay: CalendarEntity
        get() = calendarEntities[adapter.selectedPosition]

    init {
        val inflater = LayoutInflater.from(context)
        calendarViewBinding = DataBindingUtil.inflate(inflater, R.layout.view_calendar, this, true)
        calendarViewBinding.calendarView.adapter = adapter
    }


    fun initialize(year: Int, month: Int, monthlyInfo: List<CalendarEntity>) {
        //월별 뷰 초기화
        calendarEntities.clear()
        setMonthTitle(month)

        //어댑터에 해당 월 데이터 전달
        val startDay = getStartDayOfMonth(year, month)
        repeat(startDay) {
            calendarEntities.add(CalendarEntity("", ""))
        }
        calendarEntities.addAll(monthlyInfo)
        val todayPosition = calendarEntities.indexOf(getToday())
        Log.d("Calendar","todayposition: ${todayPosition}")
        calendarEntities.find {
            it == getToday()
        }?.isSelected = true


        adapter.setData(calendarEntities, todayPosition)
        calendarViewBinding.calendarView.layoutManager = getLayoutManger()
    }

    fun setDateClickListener(listener: CalendarViewAdapter.OnItemClickListener){
        adapter.setOnItemClickListener(listener)
    }

    private fun setMonthTitle(month: Int) {
        calendarViewBinding.monthNumView.text = month.toString()
        calendarViewBinding.monthEngView.text = monthIntToName(month)
    }


    private fun getLayoutManger(daysCount: Int = 7): GridLayoutManager {
        val columnSize = 1
        val layoutManager = GridLayoutManager(context, daysCount, GridLayoutManager.VERTICAL, false)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return columnSize
            }
        }
        return layoutManager
    }

    private fun getStartDayOfMonth(year: Int, month: Int) : Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1) // 월은 0부터 시작하므로 -1을 해줘야함
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2// 월 0 화 1 ... 일 -1
        return if (dayOfWeek == -1) 6 else dayOfWeek
    }

    private fun monthIntToName(month: Int): String {
        calendar.set(Calendar.MONTH, month - 1) // Calendar에서 월은 0부터 시작하므로 -1을 해줘야함
        val dateFormat = SimpleDateFormat("MMMM", Locale.ENGLISH) // MMMM: 월의 전체 이름
        return dateFormat.format(calendar.time)
    }

    private fun getToday(): CalendarEntity {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val date = String.format("%d-%02d-%02d", year, month, day)
        return CalendarEntity(date = date, status = "")
    }
}