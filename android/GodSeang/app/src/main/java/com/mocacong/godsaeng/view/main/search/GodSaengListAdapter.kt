package com.mocacong.godsaeng.view.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mocacong.godsaeng.data.Preview
import com.mocacong.godsaeng.databinding.ItemGodsaengSearchBinding

class GodSaengListAdapter : RecyclerView.Adapter<GodSaengListAdapter.MyViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null
    private var godSaengList = mutableListOf<Preview>()

    interface OnItemClickListener {
        fun onItemClick(item: Preview)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    inner class MyViewHolder(private val binding: ItemGodsaengSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(godSaeng: Preview) {
            binding.godSaengInfo = godSaeng
            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(godSaeng)
            }
        }
    }


    fun setData(godSaengs: List<Preview>) {
        godSaengList.clear()
        godSaengList.addAll(godSaengs)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemGodsaengSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = godSaengList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(godSaengList[position])
    }
}
