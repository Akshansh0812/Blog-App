package com.example.blogapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blogapp.adapter.BlogAdapter
import com.example.blogapp.databinding.ActivityMainBinding
import com.example.blogapp.model.BlogItemModel
import com.example.blogapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private val blogItems = mutableListOf<BlogItemModel>()
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // to go save article page
        binding.saveArticalButton.setOnClickListener{
            startActivity(Intent(this,SavedArticlesActivity::class.java))
        }
        //to go profile activity
        binding.profileImage.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }
        //to go profile activity
        binding.cardView.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://blog-app-40d04-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("blogs")
        val userId = auth.currentUser?.uid
        // set user profile
        if(userId != null){
            loadUserProfileImage(userId)
        }

        //set blog post into recyclerview
        //Initialize the recycler view and set adapter
        val recyclerView = binding.blogRecyclerView
        val blogAdapter = BlogAdapter(blogItems)
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // fetch data from firebase database
        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                blogItems.clear()
                for(snapshots in snapshot.children){
                    val blogItem = snapshots.getValue(BlogItemModel::class.java)
                    if(blogItem != null) {
                        blogItems.add(blogItem)
                    }
                }
                // reverse the list
                blogItems.reverse()
                // Notify the adapter that the data has changed
                blogAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Blog Loading Field", Toast.LENGTH_SHORT).show()
            }
        })

        binding.floatingAddArticleButton.setOnClickListener{
            startActivity(Intent(this, AddArticleActivity::class.java))
        }
    }
    private fun loadUserProfileImage(userID : String){
        val userReference = FirebaseDatabase.getInstance("https://blog-app-40d04-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users").child(userID)

        userReference.child("profileImage").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                if(profileImageUrl != null){
                    Glide.with(this@MainActivity)
                        .load(profileImageUrl)
                        .into(binding.profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error loading Image Profile", Toast.LENGTH_SHORT).show()
            }

        })

    }
}
