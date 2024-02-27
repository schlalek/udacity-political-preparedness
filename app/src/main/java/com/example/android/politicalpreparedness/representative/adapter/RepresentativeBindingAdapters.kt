package com.example.android.politicalpreparedness.representative.adapter

import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R


@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    if (src == null) {
        view.setImageResource(R.drawable.ic_profile)
    }
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(view.context)
            .load(uri)
            .apply(
                RequestOptions().error(R.drawable.ic_profile)
                    .placeholder(R.drawable.ic_profile)
            ).circleCrop()
            .into(view)
    }
}

@BindingAdapter("showIfTrue")
fun changeVisibility(view: ImageView, condition: Boolean?) {
    if (condition == null) {
        return
    }
    if (condition) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.INVISIBLE
    }
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}
