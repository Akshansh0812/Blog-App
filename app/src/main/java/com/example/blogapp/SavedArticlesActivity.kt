package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.adapter.BlogAdapter
import com.example.blogapp.databinding.ActivitySavedArticlesBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SavedArticlesActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySavedArticlesBinding
    private val savedBlogsArticles = mutableListOf<BlogItemModel>()
    private lateinit var blogAdapter : BlogAdapter
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            finish()
        }

        //Initialize Blog Adapter
        blogAdapter = BlogAdapter(savedBlogsArticles.filter{it.isSaved}.toMutableList())

        val recyclerView = binding.savedArticleRecyclerView
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userId = auth.currentUser?.uid
        if(userId != null){

            val userReference = FirebaseDatabase.getInstance("https://blog-app-40d04-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(userId).child("saveBlogPosts")

            userReference.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshot in snapshot.children){
                        val postId = postSnapshot.key
                        val isSaved = postSnapshot.value as Boolean
                        if(postId != null && isSaved){
                            //fetch corresponding blog item on postId using a coroutine
                            CoroutineScope(Dispatchers.IO).launch {
                                val blogItem = fetchBlogItem(postId)
                                if(blogItem != null){
                                    savedBlogsArticles.add(blogItem)

                                    launch(Dispatchers.Main){
                                        blogAdapter.updateData(savedBlogsArticles)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    private suspend fun fetchBlogItem(postId: String): BlogItemModel? {
        val blogReference = FirebaseDatabase.getInstance("https://blog-app-40d04-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("blogs")
        return try{
            val dataSnapshot = blogReference.child(postId).get().await()
            val blogData = dataSnapshot.getValue(BlogItemModel::class.java)
            blogData
        }catch(e: Exception){
            null
        }
    }
}