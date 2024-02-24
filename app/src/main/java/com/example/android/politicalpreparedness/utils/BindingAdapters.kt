package com.example.android.politicalpreparedness.utils

import android.view.View
import android.widget.Button
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

@BindingAdapter("textCondition", "textTrue", "textFalse")
fun toggleText(view: Button, textCondition: Boolean, textTrue: String, textFalse: String) {
    view.text = (if (textCondition) textTrue else textFalse)
}

@BindingAdapter("visibleIf")
fun setVisibility(view: View, visibleIf: Boolean) {
    view.visibility = if (visibleIf) View.VISIBLE else View.GONE
}

@BindingAdapter("hideIfEmpty")
fun setVisibility(view: View, hideIfEmpty: String?) {
    view.visibility = if (hideIfEmpty.isNullOrEmpty()) View.GONE else View.VISIBLE
}