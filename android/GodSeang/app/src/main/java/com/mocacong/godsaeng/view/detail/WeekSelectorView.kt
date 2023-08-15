package com.mocacong.godsaeng.view.detail

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.mocacong.godsaeng.data.CalendarEntity
import com.mocacong.godsaeng.databinding.ViewWeekSelectorBinding

class WeekSelectorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var weekBinding: ViewWeekSelectorBinding
    private val buttons: List<AppCompatButton>


    init {
        val inflater = LayoutInflater.from(context)
        weekBinding = ViewWeekSelectorBinding.inflate(inflater, this, true)

        weekBinding.apply {
            buttons = listOf(mon, tue, wed, thu, fri, sat, sun)
        }

        buttons.forEach { btn ->
            btn.setOnClickListener {
                it.isSelected = !it.isSelected
            }
        }
    }

    fun getSelectedDays(): List<String> {
        return buttons.withIndex().filter { (_, button) ->
            button.isSelected
        }.map { (index, _) ->
            CalendarEntity.DAYS[index]
        }
    }


}
