package com.example.myapplication.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityUserBinding
import com.google.firebase.auth.FirebaseAuth

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userid = intent.getStringExtra("User ID")
        val emailid = intent.getStringExtra("Email ID")
        binding.text2.text = "User Id :: $userid"
        binding.text3.text = "Email Id :: $emailid"
        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@UserActivity, LoginActivity::class.java))
            finish()

        }
    }
}