package com.example.android.politicalpreparedness.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R
import java.text.DateFormat
import java.util.Date


@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions().error(R.drawable.ic_broken_image)
                    .placeholder(R.drawable.loading_animation)
            )
            .into(imgView)
    }
}

@BindingAdapter("android:text")
fun setText(view: TextView, date: Date?) {
    if (date != null) {
        val df: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
        val localizedDate: String = df.format(date)
        view.text = localizedDate
    }
}