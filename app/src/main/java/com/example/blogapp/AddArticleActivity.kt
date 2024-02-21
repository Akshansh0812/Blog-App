package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.blogapp.databinding.ActivityAddArticleBinding
import com.example.blogapp.model.BlogItemModel
import com.example.blogapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date

class AddArticleActivity : AppCompatActivity() {
    private val binding : ActivityAddArticleBinding by lazy {
        ActivityAddArticleBinding.inflate(layoutInflater)
    }

    private val databaseReference:DatabaseReference = FirebaseDatabase.getInstance("https://blog-app-40d04-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("blog")
    private val userReference:DatabaseReference = FirebaseDatabase.getInstance("https://blog-app-40d04-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            finish()
        }
        binding.addblogbutton.setOnClickListener{
            val title = binding.blogTitle.editText?.text.toString().trim()
            val description = binding.blogDescription.editText?.text.toString().trim()

            if(title.isEmpty() || description.isEmpty()){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
            //get current user
            val user : FirebaseUser? = auth.currentUser

            if(user != null){
                val userId = user.uid
                val userName = user.displayName?:"Anonymous"
                val userImageUrl = user.photoUrl?:""

                // fetch user name and user profile from database
                userReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userData = snapshot.getValue(UserData::class.java)
                        if(userData != null){
                            val userNameFromDB = userData.name
                            val userImageUrlFromDB = userData.profileImage
                            val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

                            //create a blogItemModel
                            val blogItem = BlogItemModel(
                                title,
                                userNameFromDB,
                                currentDate,
                                description,
                                0,
                                userImageUrlFromDB
                            )
                            // generate a unique key for the blog post
                            val key = databaseReference.push().key
                            if(key != null){

                                val blogReference = databaseReference.child(key)
                                blogReference.setValue(blogItem).addOnCompleteListener{
                                    if(it.isSuccessful){
                                        finish()
                                    }
                                    else{
                                        Toast.makeText(
                                            this@AddArticleActivity,
                                            "failed to add blog",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
        }
    }
}