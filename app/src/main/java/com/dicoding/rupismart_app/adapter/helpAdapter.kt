package com.dicoding.rupismart_app.adapter



import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.rupismart_app.R
import com.dicoding.rupismart_app.databinding.ItemHelpBinding
import com.dicoding.rupismart_app.data.remote.response.CategoriesItem

class helpAdapter: ListAdapter<CategoriesItem, helpAdapter.MyViewHolder>(helpAdapter.DIFF_CALLBACK) {

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
            binding.tvTitleHelp.text = help.title
            binding.tvDescHelp.text = help.text
            binding.helpNumber.text = (index+1).toString()
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