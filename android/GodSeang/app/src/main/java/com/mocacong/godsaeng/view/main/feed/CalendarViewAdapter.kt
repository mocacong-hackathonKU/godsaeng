package com.mocacong.godsaeng.view.main.feed

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mocacong.godsaeng.data.CalendarEntity
import com.mocacong.godsaeng.databinding.ItemCalendarDateBinding

class CalendarViewAdapter : RecyclerView.Adapter<CalendarViewAdapter.CalendarViewHolder>() {

    private val calendarInfo = mutableListOf<CalendarEntity>()
    var selectedPosition = -1

    private var onItemClickListener: OnItemClickListener? = null


    interface OnItemClickListener {
        fun onItemClick(item: CalendarEntity)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    inner class CalendarViewHolder(private val binding: ItemCalendarDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(calendarEntity: CalendarEntity) {
            binding.calendarEntity = calendarEntity
            binding.calendarDate.isSelected = calendarEntity.isSelected
            updateUIByStatus(calendarEntity.status)

            if(calendarEntity.date=="") {
                binding.root.isEnabled = false
                binding.calendarDate.setOnClickListener {  }
                return
            }

            if (onItemClickListener != null) {
                binding.calendarDate.setOnClickListener {
                    onItemClickListener?.onItemClick(calendarEntity)
                    if (selectedPosition != adapterPosition) {
                        Log.d("Calendar", "if들어옴. adapterPosition: $adapterPosition")

                        // 이전 선택된 아이템의 상태를 변경
                        if (selectedPosition != -1) {
                            calendarInfo[selectedPosition].isSelected = false
                            notifyItemChanged(selectedPosition)
                        }

                        // 현재 아이템의 상태를 변경
                        calendarEntity.isSelected = true
                        notifyItemChanged(adapterPosition)

                        // 현재 선택된 위치를 업데이트
                        selectedPosition = adapterPosition
                    }
                }

            }
        }

        private fun updateUIByStatus(status: String) {
            binding.statusBar.visibility = View.VISIBLE
            when (status) {
                "PROCEEDING" -> binding.statusBar.setBackgroundColor(Color.parseColor("#FFA800"))
                "DONE" -> binding.statusBar.setBackgroundColor(Color.parseColor("#72A074"))
                else -> binding.statusBar.visibility = View.GONE
            }
        }
    }

    fun setData(calendarEntityList: List<CalendarEntity>, todayPosition: Int) {
        calendarInfo.clear()
        selectedPosition = todayPosition
        calendarInfo.addAll(calendarEntityList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding =
            ItemCalendarDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun getItemCount(): Int = calendarInfo.size

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(calendarInfo[position])
    }
}
