package com.zipper.magicimage.pag.glide

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import org.libpag.PAGFile

class PagDrawable(
    val pagFile: PAGFile
) : Drawable() {

    private val state = PagDrawableState(pagFile)

    override fun getIntrinsicWidth(): Int {
        val width = pagFile.width()
        if (width > 0) {
            return width
        }
        return pagFile.bounds.width().toInt()
    }

    override fun getIntrinsicHeight(): Int {
        val height = pagFile.height()
        if (height > 0) {
            return height
        }
        return pagFile.bounds.height().toInt()
    }

    override fun draw(canvas: Canvas) = Unit

    override fun setAlpha(alpha: Int) = Unit

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat"))
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getConstantState(): ConstantState = state

    class PagDrawableState(private val pagFile: PAGFile) : ConstantState() {
        override fun newDrawable(): Drawable {
            return PagDrawable(pagFile.copyOriginal())
        }

        override fun newDrawable(res: Resources?): Drawable {
            return PagDrawable(pagFile.copyOriginal())
        }

        override fun newDrawable(res: Resources?, theme: Resources.Theme?): Drawable {
            return PagDrawable(pagFile.copyOriginal())
        }

        override fun getChangingConfigurations(): Int = 0
    }
}