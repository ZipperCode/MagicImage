package com.zipper.magicimage

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.request.target.Target

interface MagicViewTarget : Target<Drawable> {

    fun getView(): View

    fun getScaleType(): ImageView.ScaleType

}