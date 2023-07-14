package com.example.myapplication.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityUserProfileBinding
import com.example.myapplication.firestore.FirestoreClass
import com.example.myapplication.models.User
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_settings.toolbar_settings_activity
import kotlinx.android.synthetic.main.activity_user_profile.iv_user_photo
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
        binding.firstname.setText(mUserDetails.firstName)
        binding.lastname.setText(mUserDetails.lastName)
        binding.email.isEnabled = false;
        binding.email.setText(mUserDetails.email)
        if(mUserDetails.profileCompleted == 0) {
            binding.text1.text = resources.getString(R.string.title_complete_profile)

            binding.firstname.isEnabled = false;
            binding.lastname.isEnabled = false;

        }
        else {
            setupActionBar()
            binding.text1.text = resources.getString(R.string.title_edit_profile)
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image, binding.ivUserPhoto)
            if(mUserDetails.mobile != 0L) {
                binding.mobile.setText(mUserDetails.mobile.toString())
            }
            if(mUserDetails.gender == Constants.MALE) {
                binding.rbMale.isChecked = true
            }
            else {
                binding.rbFemale.isChecked = true
            }
        }
//        binding.firstname.isEnabled = false;
//        binding.firstname.setText(mUserDetails.firstName)
//        binding.lastname.isEnabled = false;
//        binding.lastname.setText(mUserDetails.lastName)

        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        binding.submit.setOnClickListener(this@UserProfileActivity)

    }


//    companion object {
//        private const val CAMERA_PERMISSION_CODE = 1
//        private const val FINE_LOCATION_PERMISSION_CODE = 2
//    }

    override fun onClick(v: View?) {
        if(v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@UserProfileActivity)
//                        Toast.makeText(this, "You already have the storage permission.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.submit -> {
                    if(mSelectedImageFileUri != null) {
                        FirestoreClass().uploadImageToCloudStorage(this@UserProfileActivity, mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE)
                    }
                    else {
                        updateUserProfileDetails()
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()
        val firstName = binding.firstname.text.toString().trim{it <= ' '}
        if(firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }
        val lastName = binding.lastname.text.toString().trim{it <= ' '}
        if(lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }
        val mobileNumber = binding.mobile.text.toString().trim{it<= ' '}
        val gender = if(binding.rbMale.isChecked) {
            Constants.MALE
        }
        else {
            Constants.FEMALE
        }
        if(mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }
        if(mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }
        if(gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }
        userHashMap[Constants.GENDER] = gender
        userHashMap[Constants.COMPLETE_PROFILE] = 1
        FirestoreClass().updateUserProfile(this@UserProfileActivity, userHashMap)
    }

    fun userProfileUpdateSuccess() {
        Toast.makeText(this@UserProfileActivity, "Your profile is update successfully", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@UserProfileActivity)

//                Toast.makeText(this@UserProfileActivity, "The storage permission is granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this@UserProfileActivity, "Oops, you just denied the permission for storage. You can also allow it from settings.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if(data != null) {
                    try{
                        mSelectedImageFileUri = data.data!!
//                        iv_user_photo.setImageURI(selectedImageFileUri)
                        GlideLoader(this@UserProfileActivity).loadUserPicture(mSelectedImageFileUri!!, iv_user_photo)
                    }
                    catch(e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@UserProfileActivity, "Image selection failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
//        Toast.makeText(this@UserProfileActivity, "Your Image Uploaded Successfully. Image URL is $imageURL", Toast.LENGTH_SHORT).show()

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_settings_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarUserProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }
}











