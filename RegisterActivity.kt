package com.example.myapplication.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.example.myapplication.firestore.FirestoreClass
import com.example.myapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.regis.setOnClickListener {
            val email:String = binding.registeremail.text.toString().trim {it <= ' '}
            val firstName:String = binding.firstname.text.toString().trim {it <= ' '}
            val lastName:String = binding.lastname.text.toString().trim {it <= ' '}
            val password:String = binding.registerpassword.text.toString().trim(){it <= ' '}
//            val repass:String = binding.registerrepassword.text.toString().trim(){it <= ' '}
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val user = User(firebaseUser.uid, email, firstName, lastName)
                        FirestoreClass().registerUser(this@RegisterActivity, user)
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
//                        val intent = Intent(this@RegisterActivity, UserActivity::class.java)
//                        intent.flags =
//                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        intent.putExtra("User ID", firebaseUser.uid)
//                        intent.putExtra("Email ID", email)
//                        startActivity(intent)
//                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Register Unseccessful",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }
    fun userRegistrationSuccess() {
        Toast.makeText(
            this@RegisterActivity,
            "You were registered successfully.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
