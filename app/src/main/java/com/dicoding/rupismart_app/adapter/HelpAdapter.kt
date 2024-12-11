package com.dicoding.rupismart_app.adapter

import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.rupismart_app.databinding.ItemHelpBinding
import com.dicoding.rupismart_app.data.remote.response.CategoriesItem

class HelpAdapter(): ListAdapter<CategoriesItem, HelpAdapter.MyViewHolder>(HelpAdapter.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHelpBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event,position)

    }
    class MyViewHolder(private val binding: ItemHelpBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(help: CategoriesItem, index:Int){
        val number=index+1;
            binding.tvTitleHelp.text = help.title
            binding.tvDescHelp.text = help.text
            binding.helpNumber.text = (number).toString()
        }


    }
    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoriesItem>() {
            override fun areItemsTheSame(oldItem: CategoriesItem, newItem: CategoriesItem): Boolean {
                return oldItem==newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: CategoriesItem, newItem: CategoriesItem): Boolean {
                return oldItem==newItem
            }

        }
    }
}