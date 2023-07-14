package com.example.myapplication.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityAddProductBinding
import com.example.myapplication.firestore.FirestoreClass
import com.example.myapplication.models.Product
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_product.et_product_description
import kotlinx.android.synthetic.main.activity_add_product.et_product_price
import kotlinx.android.synthetic.main.activity_add_product.et_product_quantity
import kotlinx.android.synthetic.main.activity_add_product.et_product_title
import kotlinx.android.synthetic.main.activity_settings.toolbar_settings_activity
import kotlinx.android.synthetic.main.activity_user_profile.iv_user_photo
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddProductBinding
    private var mSelectedImageFileURI: Uri? = null
    private var mProductImageURL:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        binding.ivAddUpdateProduct.setOnClickListener(this@AddProductActivity)
        binding.btnSubmit.setOnClickListener(this@AddProductActivity)
    }
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddProductActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarAddProductActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id) {
                R.id.iv_add_update_product -> {
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@AddProductActivity)
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit -> {

                    uploadProductImage()
//                    if (validateProductDetails()) {

//                        uploadProductImage()
//                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddProductActivity)

//                Toast.makeText(this@UserProfileActivity, "The storage permission is granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this@AddProductActivity, "Oops, you just denied the permission for storage. You can also allow it from settings.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if(data != null) {
                    binding.ivAddUpdateProduct.setImageDrawable(ContextCompat.getDrawable(this@AddProductActivity, R.drawable.ic_vector_edit))
                    mSelectedImageFileURI = data.data!!
                    try{
                        GlideLoader(this@AddProductActivity).loadUserPicture(mSelectedImageFileURI!!, binding.ivProductImage)
                    }
                    catch(e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@AddProductActivity, "Image selection failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    fun imageUploadSuccess(imageURL: String) {
//        mUserProfileImageURL = imageURL
//        updateUserProfileDetails()
//        Toast.makeText(this@AddProductActivity, "Your Image Uploaded Successfully. Image URL is $imageURL", Toast.LENGTH_SHORT).show()
        mProductImageURL = imageURL
        uploadProductDetails()

    }

    fun productUploadSuccess() {

        // Hide the progress dialog
//        hideProgressDialog()

        Toast.makeText(
            this@AddProductActivity,
            resources.getString(R.string.product_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    private fun uploadProductDetails() {

        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username =
            this.getSharedPreferences(Constants.MYAPP_PREFERENCE, Context.MODE_PRIVATE)
                .getString(Constants.LOGGED_IN_USERNAME, "")!!

        // Here we get the text from editText and trim the space
        val product = Product(
            FirestoreClass().getCurrentUserID(),
            username,
            binding.etProductTitle.text.toString().trim { it <= ' ' },
            binding.etProductPrice.text.toString().trim { it <= ' ' },
            binding.etProductDescription.text.toString().trim { it <= ' ' },
            binding.etProductQuantity.text.toString().trim { it <= ' ' },
            mProductImageURL
        )

        FirestoreClass().uploadProductDetails(this@AddProductActivity, product)
    }

    private fun uploadProductImage() {

//        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().uploadImageToCloudStorage(
            this@AddProductActivity,
            mSelectedImageFileURI,
            Constants.PRODUCT_IMAGE
        )
    }
}





