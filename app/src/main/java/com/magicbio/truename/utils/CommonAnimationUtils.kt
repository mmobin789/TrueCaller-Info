package com.magicbio.truename.utils

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation

object CommonAnimationUtils {
    @JvmStatic
    fun applyFadeInFadeOut(v: View) {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = 500
        fadeOut.repeatCount = Animation.INFINITE
        fadeOut.repeatMode = Animation.REVERSE
        v.startAnimation(fadeOut)

    }


    @JvmStatic
    fun slideFromRightToLeft(view: View, fromXDelta: Float) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(fromXDelta, 0f, 0f, 0f) // View for animation
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    @JvmStatic
    fun slideFromLeftToRight(view: View, view1: View) {
        val animate = TranslateAnimation(0f, view1.width.toFloat(), 0f, 0f) // View for animation
        animate.duration = 500
        animate.fillAfter = true
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        view.startAnimation(animate)
        // Change visibility VISIBLE or GONE
    }
}