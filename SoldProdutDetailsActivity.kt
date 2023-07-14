package com.example.myapplication.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySoldProdutDetailsBinding
import com.example.myapplication.models.SoldProduct
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_sold_produt_details.iv_product_item_image
import kotlinx.android.synthetic.main.activity_sold_produt_details.toolbar_sold_product_details_activity
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_order_details_date
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_order_details_id
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_product_item_name
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_product_item_price
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_sold_details_additional_note
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_sold_details_address
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_sold_details_address_type
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_sold_details_full_name
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_sold_details_mobile_number
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_sold_details_other_details
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_sold_product_shipping_charge
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_sold_product_sub_total
import kotlinx.android.synthetic.main.activity_sold_produt_details.tv_sold_product_total_amount
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SoldProdutDetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySoldProdutDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoldProdutDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        var productDetails: SoldProduct = SoldProduct()

        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)) {
            productDetails =
                intent.getParcelableExtra<SoldProduct>(Constants.EXTRA_SOLD_PRODUCT_DETAILS)!!
        }
        setupUI(productDetails)
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_sold_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_sold_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }
    // END

    // TODO Step 3: Create a function to setupUI.
    // START
    /**
     * A function to setup UI.
     *
     * @param productDetails Order details received through intent.
     */
    private fun setupUI(productDetails: SoldProduct) {

        tv_order_details_id.text = productDetails.order_id

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.order_date
        tv_order_details_date.text = formatter.format(calendar.time)

        GlideLoader(this@SoldProdutDetailsActivity).loadProductPicture(
            productDetails.image,
            iv_product_item_image
        )
        tv_product_item_name.text = productDetails.title
        tv_product_item_price.text ="Rs ${productDetails.price}"
//        tv_sold_product_quantity.text = productDetails.sold_quantity

        tv_sold_details_address_type.text = productDetails.address.type
        tv_sold_details_full_name.text = productDetails.address.name
        tv_sold_details_address.text =
            "${productDetails.address.address}, ${productDetails.address.zipCode}"
        tv_sold_details_additional_note.text = productDetails.address.additionalNote

        if (productDetails.address.otherDetails.isNotEmpty()) {
            tv_sold_details_other_details.visibility = View.VISIBLE
            tv_sold_details_other_details.text = productDetails.address.otherDetails
        } else {
            tv_sold_details_other_details.visibility = View.GONE
        }
        tv_sold_details_mobile_number.text = productDetails.address.mobileNumber

        tv_sold_product_sub_total.text = productDetails.sub_total_amount
        tv_sold_product_shipping_charge.text = productDetails.shipping_charge
        tv_sold_product_total_amount.text = productDetails.total_amount
    }
}