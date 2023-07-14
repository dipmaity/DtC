package com.example.myapplication.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.firestore.FirestoreClass
import com.example.myapplication.models.User
import com.example.myapplication.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.login.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email:String = binding.loginemail.text.toString().trim {it <= ' '}
            val password:String = binding.loginpassword.text.toString().trim {it <= ' '}
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    task ->
                    if (task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LoginActivity)

//                        val firebaseUser: FirebaseUser = task.result!!.user!!
//                        Toast.makeText(this@LoginActivity, "You are logged in successfully.", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this@LoginActivity, UserActivity::class.java)
//                        intent.flags =
//                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        intent.putExtra("User ID", firebaseUser.uid)
//                        intent.putExtra("Email ID", email)
//                        startActivity(intent)
//                        finish()
                    }
                    else {
                        Toast.makeText(this@LoginActivity, "Login unsuccessful", Toast.LENGTH_SHORT).show()

                    }
                }
        }
    }

    fun userLoggedInSuccess(user: User) {
//        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//        finish()
        hideProgressDialog()
        if(user.profileCompleted == 0) {

            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        }
        else {
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()
    }
}











