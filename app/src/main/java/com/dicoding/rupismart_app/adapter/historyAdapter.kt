package com.dicoding.rupismart_app.adapter


import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.rupismart_app.R
import com.dicoding.rupismart_app.data.local.entity.HistoryEntity
import com.dicoding.rupismart_app.databinding.ItemHistoryBinding
import com.dicoding.rupismart_app.utils.formatTimestamp

class historyAdapter: ListAdapter<HistoryEntity, historyAdapter.MyViewHolder>(historyAdapter.DIFF_CALLBACK) {

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
        fun bind(history: HistoryEntity){
            val context = binding.root.context



            binding.tvItem.text = if(history.isKoin) context.getString(R.string.not_coin) else context.getString(R.string.coin)
            binding.tvNominal.text = history.nominal.toString()
            binding.tvDateTime.text = history.timestamp.formatTimestamp(context)
            val tvDesc = binding.tvItem
            tvDesc.paintFlags = tvDesc.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }


    }
    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryEntity>() {
            override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                return oldItem==newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                return oldItem==newItem
            }

        }
    }
}