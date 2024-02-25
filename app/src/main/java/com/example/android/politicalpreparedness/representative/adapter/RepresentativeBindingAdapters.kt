package com.example.android.politicalpreparedness.representative.adapter

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R


@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(view.context)
            .load(view)
            .apply(
                RequestOptions().error(R.drawable.ic_profile)
                    .placeholder(R.drawable.ic_profile)
            ).circleCrop()
            .into(view)
    }
}

@BindingAdapter("showIfTrue")
fun changeVisibility(view: View, condition: Boolean?) {
    if (condition == null) return
    view.visibility = if (condition == true) View.VISIBLE else View.GONE
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

@InverseBindingAdapter(attribute = "stateValue", event = "selectedStateAttrChanged")
fun Spinner.getStateValue(): String {
    return selectedItem as String
}

@BindingAdapter(value = ["selectedStateAttrChanged"], requireAll = false)
fun bindStateChanged(
    view: Spinner,
    newTextAttrChanged: InverseBindingListener
) {
    val listener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            newTextAttrChanged.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            newTextAttrChanged.onChange()
        }
    }
    view.onItemSelectedListener = listener
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}
