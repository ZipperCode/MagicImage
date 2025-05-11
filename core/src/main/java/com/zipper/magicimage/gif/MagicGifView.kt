package com.zipper.magicimage.gif

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.zipper.magicimage.AnimateEndIntent
import com.zipper.magicimage.AnimateStartIntent
import com.zipper.magicimage.IMagicIntent
import com.zipper.magicimage.MagicParam
import com.zipper.magicimage.MagicView
import com.zipper.magicimage.OnLoadClearedIntent
import com.zipper.magicimage.OnLoadFailedIntent
import com.zipper.magicimage.OnLoadStartedIntent
import com.zipper.magicimage.OnStartIntent
import com.zipper.magicimage.OnStopIntent
import com.zipper.magicimage.PauseHideIntent

class MagicGifView(
    private val internalImageView: ImageView,
    private val param: MagicParam
) : MagicView, View.OnAttachStateChangeListener {

    private val gifImageView = ImageView(internalImageView.context).apply {
        addOnAttachStateChangeListener(this@MagicGifView)
    }

    private var resumePlay = false

    private val animateCalendar = object : Animatable2Compat.AnimationCallback(){
        override fun onAnimationStart(drawable: Drawable?) {
            super.onAnimationStart(drawable)
            param.animateIntentCallback(AnimateStartIntent)
        }

        override fun onAnimationEnd(drawable: Drawable?) {
            super.onAnimationEnd(drawable)
            param.animateIntentCallback(AnimateEndIntent)
        }
    }

    override fun getView(): View = gifImageView

    override fun onResourceReady(resource: Drawable): Boolean {
        if (resource !is GifDrawable) {
            return false
        }
        resource.setLoopCount(param.animateRepeatCount)
        resource.unregisterAnimationCallback(animateCalendar)
        resource.registerAnimationCallback(animateCalendar)
        if (param.autoAnim) {
            gifImageView.setImageDrawable(resource)
            resource.start()
        } else {
            gifImageView.visibility = View.GONE
            // 首帧
            internalImageView.setImageBitmap(resource.firstFrame)
            internalImageView.post {
                // 返回true之后会隐藏internalImageView
                internalImageView.visibility = View.VISIBLE
            }
        }
        return true
    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        gifImageView.scaleType = scaleType
    }

    override fun onViewAttachedToWindow(v: View) {
        val drawable = gifImageView.drawable
        if (drawable is GifDrawable) {
            drawable.registerAnimationCallback(animateCalendar)
        }
        processIntent(OnStartIntent)
    }

    override fun onViewDetachedFromWindow(v: View) {
        val drawable = gifImageView.drawable
        if (drawable is GifDrawable) {
            drawable.unregisterAnimationCallback(animateCalendar)
        }
        processIntent(OnStopIntent)
    }

    override fun processIntent(intent: IMagicIntent) {
        val drawable = gifImageView.drawable
        when (intent) {
            is OnStartIntent -> {
                if (drawable is GifDrawable && (param.autoAnim || resumePlay)) {
                    gifImageView.visibility = View.VISIBLE
                    internalImageView.visibility = View.GONE
                    drawable.start()
                }
            }

            is OnStopIntent -> {
                if (drawable is GifDrawable) {
                    resumePlay = drawable.isRunning
                    drawable.stop()
                }
            }

            is OnLoadStartedIntent,
            is OnLoadFailedIntent,
            is OnLoadClearedIntent,
            is PauseHideIntent -> {
                gifImageView.visibility = View.GONE
                if (drawable is GifDrawable) {
                    drawable.stop()
                }
            }

            else -> Unit
        }
    }
}