package com.example.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.blogapp.databinding.ActivityEditArticleBinding
import com.example.blogapp.databinding.ActivityEditBlogBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.database.FirebaseDatabase

class EditBlog : AppCompatActivity() {
    private lateinit var binding : ActivityEditBlogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            finish()
        }
        val blogItemModel = intent.getParcelableExtra<BlogItemModel>("blogItem")

        binding.blogTitle.editText?.setText(blogItemModel?.heading)
        binding.blogDescription.editText?.setText(blogItemModel?.post)

        binding.saveBlogButton.setOnClickListener {
            val updatedTitle = binding.blogTitle.editText?.text.toString().trim()
            val updatedDescription = binding.blogDescription.editText?.text.toString().trim()

            if(updatedTitle.isEmpty() || updatedDescription.isEmpty()){
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            }
            else{
                blogItemModel?.heading = updatedTitle
                blogItemModel?.post = updatedDescription

                if(blogItemModel != null){
                    updateDataInFireBase(blogItemModel)
                }
            }
        }
    }

    private fun updateDataInFireBase(blogItemModel: BlogItemModel) {

        val databaseReference = FirebaseDatabase.getInstance("https://blog-app-40d04-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("blogs")

        val postId = blogItemModel.postId
        databaseReference.child(postId).setValue(blogItemModel)
            .addOnSuccessListener {
                Toast.makeText(this, "Blog Updated Successful", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Blog Updated Unsuccessful", Toast.LENGTH_SHORT).show()
            }
    }
}