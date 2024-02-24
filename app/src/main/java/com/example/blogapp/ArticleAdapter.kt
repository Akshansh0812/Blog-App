package com.example.blogapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.databinding.ArticleEditItemBinding
import com.example.blogapp.model.BlogItemModel

class ArticleAdapter(
    private val context: Context,
    private var blogList:List<BlogItemModel>,
    private val itemClickListener: OnItemClickListener
    ) : RecyclerView.Adapter<ArticleAdapter.BlogViewHolder>(){

    interface OnItemClickListener{
        fun onEditClick(blogItem: BlogItemModel)
        fun onReadMoreClick(blogItem: BlogItemModel)
        fun onDeleteClick(blogItem: BlogItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ArticleEditItemBinding.inflate(inflater,parent,false)
        return BlogViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = blogList[position]
        holder.bind(blogItem)
    }

    fun setData(blogSavedList: ArrayList<BlogItemModel>) {
        this.blogList = blogSavedList
        notifyDataSetChanged()
    }

    inner class BlogViewHolder(private val binding:ArticleEditItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItem: BlogItemModel) {
            binding.heading.text = blogItem.heading
            Glide.with(binding.profile.context)
                .load(blogItem.profileUrl)
                .into(binding.profile)
            binding.userName.text = blogItem.userName
            binding.date.text = blogItem.date
            binding.post.text = blogItem.post

            // handle read more click
            binding.readmorebutton.setOnClickListener {
                itemClickListener.onReadMoreClick(blogItem)
            }
            // handle edit click
            binding.editButton.setOnClickListener {
                itemClickListener.onEditClick(blogItem)
            }
            // handle delete click
            binding.deleteButton.setOnClickListener {
                itemClickListener.onDeleteClick(blogItem)
            }
        }
    }
}