package com.example.blogapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.databinding.ActivitySignInAndRegistrationBinding
import com.example.blogapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SignInAndRegistrationActivity : AppCompatActivity() {

    private val binding : ActivitySignInAndRegistrationBinding by lazy{
        ActivitySignInAndRegistrationBinding.inflate(layoutInflater)
    }
    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://blog-app-40d04-default-rtdb.asia-southeast1.firebasedatabase.app")
        storage = FirebaseStorage.getInstance()

        // for visibility of fields
        val action = intent.getStringExtra("action")
        if(action == "login"){
            binding.loginEmailAddress.visibility = View.VISIBLE
            binding.loginPassword.visibility = View.VISIBLE
            binding.loginButton.visibility = View.VISIBLE
            binding.registerButton.visibility = View.INVISIBLE
            binding.cardView2.visibility = View.GONE
            binding.registerName.visibility = View.GONE
            binding.registerEmail.visibility = View.GONE
            binding.registerPassword.visibility = View.GONE
            binding.registerNewHere.visibility = View.INVISIBLE


            binding.loginButton.setOnClickListener{
                val loginEmail = binding.loginEmailAddress.text.toString()
                val loginPassword = binding.loginPassword.text.toString()
                if(loginEmail.isEmpty() || loginPassword.isEmpty()){
                    Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
                }
                else{
                    auth.signInWithEmailAndPassword(loginEmail,loginPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Login Failed.Please Enter Correct Details", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
        else if (action == "register"){
            binding.loginButton.isEnabled = false
            binding.loginButton.alpha = 0.5f

            binding.registerButton.setOnClickListener{

                val registerName = binding.registerName.text.toString()
                val registerEmail = binding.registerEmail.text.toString()
                val registerPassword = binding.registerPassword.text.toString()

                if(registerName.isEmpty() || registerEmail.isEmpty() || registerPassword.isEmpty()){
                    Toast.makeText(this, "Please Fill All The Details", Toast.LENGTH_SHORT).show()
                }
                else{
                    auth.createUserWithEmailAndPassword(registerEmail, registerPassword)
                        .addOnCompleteListener { task->
                            if(task.isSuccessful){
                                val user = auth.currentUser
                                auth.signOut()
                                user?.let{
                                    //Save user data into Firebase realtime database
                                    val userReference = database.getReference("users")
                                    val userId = user.uid
                                    val userData = UserData(
                                        registerName,
                                        registerEmail
                                    )
                                    userReference.child(userId).setValue(userData)
//                                        .addOnSuccessListener {
//                                            Log.d("TAG", "onCreate : Data saved")
//                                        }
//                                        .addOnFailureListener{e->
//                                            Log.e("TAG", "Error saving data ${e.message}")
//
//                                        }
                                    // upload image to firebase storage
                                    val storageReference = storage.reference.child("profile_image/$userId.jpg")
                                    storageReference.putFile(imageUri!!)
                                        .addOnCompleteListener{task->
                                            if(task.isSuccessful){
                                                storageReference.downloadUrl.addOnCompleteListener{imageUri->
                                                    val imageUrl = imageUri.result.toString()
                                                    //save the image uri to the realtime database
                                                    userReference.child(userId).child("profileImage").setValue(imageUrl)
                                            }
                                        }

                                    }
                                    Toast.makeText(this, "Successful",Toast.LENGTH_SHORT ).show()
                                    startActivity(Intent(this, WelcomeActivity::class.java))
                                    finish()
                                }
                            }
                            else{
                                Toast.makeText(this, "Failed",Toast.LENGTH_SHORT ).show()
                            }
                        }
                }
            }
        }
        // set on click listener for the choose image
        binding.cardView2.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "select Image"),
                    PICK_IMAGE_REQUEST
                )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.registerUserImage)
        }
    }
}