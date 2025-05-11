package com.zipper.magicimage.svga

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.opensource.svgaplayer.SVGACallback
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.zipper.magicimage.AnimateEndIntent
import com.zipper.magicimage.AnimateRepeatIntent
import com.zipper.magicimage.AnimateStartIntent
import com.zipper.magicimage.AnimateUpdateIntent
import com.zipper.magicimage.IMagicIntent
import com.zipper.magicimage.MagicParam
import com.zipper.magicimage.MagicView
import com.zipper.magicimage.OnLoadClearedIntent
import com.zipper.magicimage.OnLoadFailedIntent
import com.zipper.magicimage.OnLoadStartedIntent
import com.zipper.magicimage.OnStartIntent
import com.zipper.magicimage.OnStopIntent
import com.zipper.magicimage.PauseHideIntent
import com.zipper.magicimage.svga.glide.SvgaDrawableWrap

class MagicSvgaView(
    private val context: Context,
    private val param: MagicParam
) : MagicView, View.OnAttachStateChangeListener {

    private val svgaView = SVGAImageView(context).apply {
        addOnAttachStateChangeListener(this@MagicSvgaView)
    }

    private var resumePlay = false

    private val svgaCallback = object : SVGACallback {
        override fun onRepeat() {
            param.animateIntentCallback(AnimateRepeatIntent)
        }

        override fun onPause() = Unit

        override fun onFinished() {
            param.animateIntentCallback(AnimateEndIntent)
        }

        override fun onStep(frame: Int, percentage: Double) {
            param.animateIntentCallback(AnimateUpdateIntent)
        }
    }

    override fun getView(): View = svgaView

    override fun onResourceReady(resource: Drawable): Boolean {
        if (resource !is SvgaDrawableWrap) {
            return false
        }
        svgaView.callback = svgaCallback
        svgaView.loops = param.animateRepeatCount
        svgaView.setImageDrawable(resource.wrapDrawable)
        if (param.autoAnim) {
            svgaView.startAnimation()
            param.animateIntentCallback(AnimateStartIntent)
        } else {
            svgaView.stepToFrame(1, false)
        }
        return true
    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        svgaView.scaleType = scaleType
    }

    override fun onViewAttachedToWindow(v: View) {
        svgaView.callback = svgaCallback
        processIntent(OnStartIntent)
    }

    override fun onViewDetachedFromWindow(v: View) {
        svgaView.callback = null
        processIntent(OnStopIntent)
    }

    override fun processIntent(intent: IMagicIntent) {
        val drawable = svgaView.drawable
        when (intent) {
            is OnStartIntent -> {
                if (drawable is SVGADrawable && (param.autoAnim || resumePlay)) {
                    svgaView.startAnimation()
                    param.animateIntentCallback(AnimateStartIntent)
                }
            }

            is OnStopIntent -> {
                resumePlay = svgaView.isAnimating
                svgaView.stopAnimation()
            }

            is OnLoadStartedIntent,
            is OnLoadFailedIntent,
            is OnLoadClearedIntent,
            is PauseHideIntent -> {
                svgaView.visibility = View.GONE
                svgaView.stopAnimation()
            }

            else -> Unit
        }
    }
}