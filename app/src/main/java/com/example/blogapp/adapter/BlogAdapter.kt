package com.example.blogapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.ReadMoreActivity
import com.example.blogapp.databinding.BlogItemBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BlogAdapter(private val items: List<BlogItemModel>): RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private val databaseReference:DatabaseReference = FirebaseDatabase.getInstance("https://blog-app-40d04-default-rtdb.asia-southeast1.firebasedatabase.app").reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BlogItemBinding.inflate(inflater,parent,false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class BlogViewHolder(private val binding : BlogItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(blogItemModel: BlogItemModel){
            val postId = blogItemModel.postId
            binding.heading.text = blogItemModel.heading
            Glide.with(binding.profile.context)
                .load(blogItemModel.profileUrl)
                .into(binding.profile)
            binding.userName.text = blogItemModel.userName
            binding.date.text = blogItemModel.date
            binding.post.text = blogItemModel.post
            binding.likeCount.text = blogItemModel.likeCount.toString()

            //set on click listener
            binding.readmorebutton.setOnClickListener{
                val context = binding.root.context
                val intent = Intent(context, ReadMoreActivity::class.java)
                intent.putExtra("blogItem", blogItemModel)
                context.startActivity(intent)
            }
            //check if the current user has liked the post and update the like button image
            val postLikeReference = databaseReference.child("blogs").child(postId).child("likes")
            val currentUserLiked = currentUser?.uid?.let{ uid->
                postLikeReference.child(uid).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            binding.likeButton.setImageResource(R.drawable.)
                        }
                        else{
                            binding.likeButton.setImageResource(R.drawable.heart_white)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }
        }
    }
}