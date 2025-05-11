package com.zipper.magicimage.pag

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.zipper.magicimage.AnimateCancelIntent
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
import com.zipper.magicimage.pag.glide.PagDrawable
import org.libpag.PAGImageView
import org.libpag.PAGScaleMode

class MagicPagView(
    context: Context,
    private val param: MagicParam
) : MagicView, View.OnAttachStateChangeListener {

    private val pagImageView = PAGImageView(context).apply {
        addOnAttachStateChangeListener(this@MagicPagView)
    }

    private var resumePlay = false

    private val listener = object : PAGImageView.PAGImageViewListener {
        override fun onAnimationStart(p0: PAGImageView?) {
            param.animateIntentCallback(AnimateStartIntent)
        }

        override fun onAnimationCancel(p0: PAGImageView?) {
            param.animateIntentCallback(AnimateCancelIntent)
        }

        override fun onAnimationEnd(p0: PAGImageView?) {
            param.animateIntentCallback(AnimateEndIntent)
        }

        override fun onAnimationRepeat(p0: PAGImageView?) {
            param.animateIntentCallback(AnimateRepeatIntent)
        }

        override fun onAnimationUpdate(p0: PAGImageView?) {
            param.animateIntentCallback(AnimateUpdateIntent)
        }
    }

    override fun getView(): View = pagImageView

    override fun onResourceReady(resource: Drawable): Boolean {
        if (resource !is PagDrawable) {
            return false
        }
        pagImageView.setRepeatCount(param.animateRepeatCount)
        pagImageView.removeListener(listener)
        pagImageView.addListener(listener)
        pagImageView.composition = resource.pagFile
        if (param.autoAnim) {
            // auto play
            pagImageView.play()
        } else {
            pagImageView.post {
                // first frame
                pagImageView.setCurrentFrame(0)
            }
        }
        return true
    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        when (scaleType) {
            ImageView.ScaleType.FIT_XY -> {
                // 对应pag的 Stretch 拉伸到填满大小
                pagImageView.setScaleMode(PAGScaleMode.Stretch)
            }

            ImageView.ScaleType.FIT_CENTER -> {
                // 对应Pag的 LetterBox 保持宽高比到可用大小，留出部分黑边
                pagImageView.setScaleMode(PAGScaleMode.LetterBox)
            }

            ImageView.ScaleType.FIT_START -> {
                // 对应Pag的 None 从左上角开始裁剪
                pagImageView.setScaleMode(PAGScaleMode.None)
            }

            ImageView.ScaleType.CENTER_CROP -> {
                // 对应Pag的 Zoom 缩放到合适大小，会进行裁剪
                pagImageView.setScaleMode(PAGScaleMode.Zoom)
            }

            else -> Unit
        }
    }

    override fun onViewAttachedToWindow(v: View) {
        pagImageView.addListener(listener)
        processIntent(OnStartIntent)
    }

    override fun onViewDetachedFromWindow(v: View) {
        pagImageView.removeListener(listener)
        processIntent(OnStopIntent)
    }


    override fun processIntent(intent: IMagicIntent) {
        when (intent) {
            is OnStartIntent -> {
                if (pagImageView.composition != null && (param.autoAnim || resumePlay)) {
                    pagImageView.play()
                }
            }

            is OnStopIntent -> {
                resumePlay = pagImageView.isPlaying
                pagImageView.pause()
            }

            is OnLoadStartedIntent,
            is OnLoadFailedIntent,
            is OnLoadClearedIntent,
            is PauseHideIntent -> {
                pagImageView.visibility = View.GONE
                pagImageView.pause()
            }

            else -> Unit
        }
    }


}