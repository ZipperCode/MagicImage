package com.zipper.magicimage

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView

interface MagicView {
    /**
     * 扩展View
     */
    fun getView(): View

    /**
     * 资源加载完成后
     */
    fun onResourceReady(resource: Drawable): Boolean

    /**
     * ScaleType
     */
    fun setScaleType(scaleType: ImageView.ScaleType)

    /**
     * Intent
     */
    fun processIntent(intent: IMagicIntent)

}