package com.example.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.blogapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // to go save article page
        binding.addNewBlogButton.setOnClickListener{
            startActivity(Intent(this,AddArticleActivity::class.java))
        }
        //to log out
        binding.LogOutButton.setOnClickListener{
            auth.signOut()
            //navigate
            startActivity(Intent(this,WelcomeActivity::class.java))
            finish()
        }
        // to go to edit article activity
        binding.articlesButton.setOnClickListener{
            startActivity(Intent(this, EditArticleActivity::class.java))
        }
        //Initialize FireBase
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://blog-app-40d04-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users")
        val userId = auth.currentUser?.uid
        if(userId != null){
            loadUserProfileData(userId)
        }
    }

    private fun loadUserProfileData(userId: String) {
        val userReference = databaseReference.child(userId)

        // Load user profile Image
        userReference.child("profileImage").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                if(profileImageUrl != null){
                    Glide.with(this@ProfileActivity)
                        .load(profileImageUrl)
                        .into(binding.userProfile)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Error loading user image ", Toast.LENGTH_SHORT).show()
            }

        })
        // Load user name
        userReference.child("name").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var userName = snapshot.getValue(String::class.java)
                if(userName != null){
                    binding.userName.text = userName
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}