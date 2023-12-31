package com.example.myapplication.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import java.io.IOException

class GlideLoader(val context: Context) {
    fun loadUserPicture(image: Any, imageView: ImageView) {
        try {
            Glide.with(context).load(image).into(imageView)
        }
        catch(e:IOException){
            e.printStackTrace()
        }
    }

    fun loadProductPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView.
            Glide
                .with(context)
                .load(image) // Uri or URL of the image
                 // Scale type of the image.
                .into(imageView) // the view in which the image will be loaded.
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
