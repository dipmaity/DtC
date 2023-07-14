package com.example.myapplication.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityAddEditAddressBinding
import com.example.myapplication.firestore.FirestoreClass
import com.example.myapplication.models.Address
import com.example.myapplication.utils.Constants
import kotlinx.android.synthetic.main.activity_add_edit_address.btn_submit_address
import kotlinx.android.synthetic.main.activity_add_edit_address.et_additional_note
import kotlinx.android.synthetic.main.activity_add_edit_address.et_address
import kotlinx.android.synthetic.main.activity_add_edit_address.et_full_name
import kotlinx.android.synthetic.main.activity_add_edit_address.et_other_details
import kotlinx.android.synthetic.main.activity_add_edit_address.et_phone_number
import kotlinx.android.synthetic.main.activity_add_edit_address.et_zip_code
import kotlinx.android.synthetic.main.activity_add_edit_address.rb_home
import kotlinx.android.synthetic.main.activity_add_edit_address.rb_office
import kotlinx.android.synthetic.main.activity_add_edit_address.rb_other
import kotlinx.android.synthetic.main.activity_add_edit_address.rg_type
import kotlinx.android.synthetic.main.activity_add_edit_address.toolbar_add_edit_address_activity
import kotlinx.android.synthetic.main.activity_add_edit_address.tv_title
import kotlinx.android.synthetic.main.activity_settings.toolbar_settings_activity

class AddEditAddressActivity : BaseActivity() {
    private lateinit var binding: ActivityAddEditAddressBinding
    private var mAddressDetails: Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails =
                intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)!!
        }
        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {

                tv_title.text = resources.getString(R.string.title_edit_address)
                btn_submit_address.text = resources.getString(R.string.btn_lbl_update)

                et_full_name.setText(mAddressDetails?.name)
                et_phone_number.setText(mAddressDetails?.mobileNumber)
                et_address.setText(mAddressDetails?.address)
                et_zip_code.setText(mAddressDetails?.zipCode)
                et_additional_note.setText(mAddressDetails?.additionalNote)

                when (mAddressDetails?.type) {
                    Constants.HOME -> {
                        rb_home.isChecked = true
                    }
                    Constants.OFFICE -> {
                        rb_office.isChecked = true
                    }
                    else -> {
                        rb_other.isChecked = true
                        et_other_details.visibility = View.VISIBLE
                        et_other_details.setText(mAddressDetails?.otherDetails)
                    }
                }
            }
        }


        btn_submit_address.setOnClickListener {
            saveAddressToFirestore()
        }
        rg_type.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                et_other_details.visibility = View.VISIBLE
            } else {
                et_other_details.visibility = View.GONE
            }
        }
    }
    private fun setupActionBar() {
        setSupportActionBar(toolbar_add_edit_address_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateData(): Boolean {
        return when {

            TextUtils.isEmpty(et_full_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_phone_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }

            TextUtils.isEmpty(et_zip_code.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }

            rb_other.isChecked && TextUtils.isEmpty(
                et_zip_code.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }


    private fun saveAddressToFirestore() {

        // Here we get the text from editText and trim the space
        val fullName: String = et_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber: String = et_phone_number.text.toString().trim { it <= ' ' }
        val address: String = et_address.text.toString().trim { it <= ' ' }
        val zipCode: String = et_zip_code.text.toString().trim { it <= ' ' }
        val additionalNote: String = et_additional_note.text.toString().trim { it <= ' ' }
        val otherDetails: String = et_other_details.text.toString().trim { it <= ' ' }

        if (validateData()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                rb_home.isChecked -> {
                    Constants.HOME
                }
                rb_office.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }

            // TODO Step 7: Prepare address info in data model class.
            // START
            val addressModel = Address(
                FirestoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )


            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                FirestoreClass().updateAddress(
                    this@AddEditAddressActivity,
                    addressModel,
                    mAddressDetails!!.id
                )
            } else {
                FirestoreClass().addAddress(this@AddEditAddressActivity, addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess() {

        // Hide progress dialog
        hideProgressDialog()
        val notifySuccessMessage:String = if(mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.msg_your_address_updated_successfully)
        }
        else {
            resources.getString(R.string.err_your_address_added_successfully)
        }
        Toast.makeText(
            this@AddEditAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()
    }



}