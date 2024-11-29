package com.dicoding.rupismart_app.adapter


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.rupismart_app.databinding.ItemHistoryBinding
import com.dicoding.rupismart_app.data.remote.response.HistoryItem

import java.text.SimpleDateFormat

class historyAdapter: ListAdapter<HistoryItem, historyAdapter.MyViewHolder>(historyAdapter.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        holder.itemView.setOnClickListener{
//            val intent = Intent(holder.itemView.context, DetailEventActivity::class.java)
//            intent.putExtra("event_id",event.id)
//            holder.itemView.context.startActivity(intent)
        }

    }
    class MyViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryItem){
            binding.tvNominal.text = history.nominal.toString()
            val tvDesc = binding.tvDesc
            tvDesc.paintFlags = tvDesc.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }


    }
    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryItem>() {
            override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
                return oldItem==newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
                return oldItem==newItem
            }

        }
    }
}