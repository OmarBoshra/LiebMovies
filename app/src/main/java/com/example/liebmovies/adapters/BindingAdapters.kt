package com.example.liebmovies.adapters

import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.BindingAdapter

object BindingAdapter {

@BindingAdapter("app:textwatcher")
@JvmStatic fun textWatcher(view: EditText, textwatcher: TextWatcher) {
    view.addTextChangedListener(textwatcher)
}
}