package com.zipper.magicimage.svga.glide

import com.bumptech.glide.load.engine.Resource

/**
 * 避免资源复用导致只能存在一个SVGA动画播放
 */
class SvgaResource(
    private val drawable: SvgaDrawableWrap
) : Resource<SvgaDrawableWrap> {
    override fun getResourceClass(): Class<SvgaDrawableWrap> = SvgaDrawableWrap::class.java

    override fun get(): SvgaDrawableWrap {
        return drawable.constantState.newDrawable() as SvgaDrawableWrap
    }

    override fun getSize(): Int = 1

    override fun recycle() {

    }
}