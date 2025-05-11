package com.zipper.magicimage.svga.glide

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAVideoEntity
import java.io.File

/**
 * 对SVGADrawable做包装，一方面在Glide资源复用的时候能够复制一个新的Drawable，解决同动画文件同时只能播放一个的问题
 */
class SvgaDrawableWrap(
    private val svgaFile: SvgaFile
) : Drawable() {

    private val state = State(svgaFile)

    val wrapDrawable = SVGADrawable(
        SVGAVideoEntity(
            svgaFile.movieEntity, File(svgaFile.cacheFilePath), svgaFile.width, svgaFile.height
        )
    )

    override fun getIntrinsicWidth(): Int = svgaFile.width

    override fun getIntrinsicHeight(): Int = svgaFile.height

    override fun draw(canvas: Canvas) = Unit

    override fun setAlpha(alpha: Int) = Unit
    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.TRANSPARENT", "android.graphics.PixelFormat"))
    override fun getOpacity(): Int = PixelFormat.TRANSPARENT

    override fun getConstantState(): ConstantState = state

    private class State(
        private val svgaFile: SvgaFile
    ) : Drawable.ConstantState() {
        override fun newDrawable(): Drawable = SvgaDrawableWrap(svgaFile)

        override fun newDrawable(res: Resources?): Drawable = SvgaDrawableWrap(svgaFile)

        override fun newDrawable(res: Resources?, theme: Resources.Theme?): Drawable {
            return SvgaDrawableWrap(svgaFile)
        }

        override fun getChangingConfigurations(): Int = 0
    }
}