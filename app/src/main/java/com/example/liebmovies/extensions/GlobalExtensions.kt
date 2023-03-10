package com.example.liebmovies.extensions

import android.app.Dialog
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.transition.Visibility
import android.view.View
import android.view.ViewGroup


fun View.fadeOut(duration: Long = 700, dialog: Dialog) {
    val transition: Transition = Fade(Visibility.MODE_OUT)
    transition.duration = duration
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
    this.visibility = View.GONE
    dialog.dismiss()
}

fun View.fadeIn(duration: Long = 100, dialog: Dialog) {
    val transition: Transition = Fade(Visibility.MODE_IN)
    transition.duration = duration
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
    this.visibility = View.VISIBLE
    dialog.show()
}