package com.example.liebmovies.customwidgets

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StyleRes
import com.example.liebmovies.R
import com.example.liebmovies.databinding.WidgetProgressbarBinding


class MoviesProgressBar @JvmOverloads constructor(
    context: Context, @StyleRes themeResId: Int = 0
) : Dialog(context, themeResId) {

    private var binding: WidgetProgressbarBinding =
        WidgetProgressbarBinding.inflate(LayoutInflater.from(context))

    init {
        setContentView(binding.root)
    }

    fun setSuccess() {
        binding.message.text = context.getString(R.string.loading_success)
    }

    fun setFailure() {
        binding.message.text = context.getString(R.string.no_local)
    }

    fun setLoading() {
        binding.message.text = context.getString(R.string.loading)
    }

    fun setGetFromLocalStorage() {
        binding.message.text = context.getString(R.string.no_network)
    }

    fun getRootView(): View {
        return binding.root
    }


}