package com.example.myapplication.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSoldProductBinding
import com.example.myapplication.firestore.FirestoreClass
import com.example.myapplication.models.SoldProduct
import com.myshoppal.ui.adapters.SoldProductsListAdapter
import kotlinx.android.synthetic.main.fragment_sold_product.rv_sold_product_items
import kotlinx.android.synthetic.main.fragment_sold_product.tv_no_sold_products_found

class SoldProductFragment : BaseFragment() {
    private lateinit var binding:FragmentSoldProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSoldProductBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onResume() {
        super.onResume()

        getSoldProductsList()
    }

    // TODO Step 4: Create a function to get the list of sold products.
    // START
    private fun getSoldProductsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getSoldProductsList(this@SoldProductFragment)
    }
    // END

    // TODO Step 2: Create a function to get the success result list of sold products.
    // START
    /**
     * A function to get the list of sold products.
     */
    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {

        // Hide Progress dialog.
        hideProgressDialog()

        // TODO Step 7: Populate the list in the RecyclerView using the adapter class.
        // START
        if (soldProductsList.size > 0) {
            rv_sold_product_items.visibility = View.VISIBLE
            tv_no_sold_products_found.visibility = View.GONE

            rv_sold_product_items.layoutManager = LinearLayoutManager(activity)
            rv_sold_product_items.setHasFixedSize(true)

            val soldProductsListAdapter =
                SoldProductsListAdapter(requireActivity(), soldProductsList)
            rv_sold_product_items.adapter = soldProductsListAdapter
        } else {
            rv_sold_product_items.visibility = View.GONE
            tv_no_sold_products_found.visibility = View.VISIBLE
        }
        // END
    }


}