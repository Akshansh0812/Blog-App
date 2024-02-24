package com.example.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blogapp.databinding.ActivityEditArticleBinding
import com.example.blogapp.model.BlogItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EditArticleActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditArticleBinding
    private lateinit var databaseReference: DatabaseReference
    private val auth = FirebaseAuth.getInstance()
    private lateinit var articleAdapter: ArticleAdapter
    private val EDIT_BLOG_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            finish()
        }
        val currentUserId = auth.currentUser?.uid
        val recyclerView = binding.articleRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        if(currentUserId != null){
            articleAdapter = ArticleAdapter(this, emptyList(), object:ArticleAdapter.OnItemClickListener{
                override fun onEditClick(blogItem: BlogItemModel) {
                    val intent = Intent(this@EditArticleActivity, EditBlog::class.java)
                    intent.putExtra("blogItem", blogItem)
                    startActivityForResult(intent, EDIT_BLOG_REQUEST_CODE)
                }

                override fun onReadMoreClick(blogItem: BlogItemModel) {
                    val intent = Intent(this@EditArticleActivity, ReadMoreActivity::class.java)
                    intent.putExtra("blogItem", blogItem)
                    startActivity(intent)
                }

                override fun onDeleteClick(blogItem: BlogItemModel) {
                    deleteBlogPost(blogItem)
                }
            })
        }

        recyclerView.adapter = articleAdapter

        //get saved blog data from database
        databaseReference = FirebaseDatabase.getInstance("https://blog-app-40d04-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("blogs")
        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val blogSavedList = ArrayList<BlogItemModel>()
                for(postSnapshot in snapshot.children){
                    val blogSaved = postSnapshot.getValue(BlogItemModel::class.java)
                    if(blogSaved != null && currentUserId == blogSaved.userId){
                        blogSavedList.add(blogSaved)
                    }
                }
                articleAdapter.setData(blogSavedList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditArticleActivity, "Error Loading Saved Blogs", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun deleteBlogPost(blogItem: BlogItemModel) {
        val postId = blogItem.postId
        val blogPostReference = databaseReference.child(postId)

        // remove the blog post
        blogPostReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this@EditArticleActivity, "Blog Post Deleted Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this@EditArticleActivity, "Blog Post Deleted Unsuccessfully", Toast.LENGTH_SHORT).show()
            }
    }
}