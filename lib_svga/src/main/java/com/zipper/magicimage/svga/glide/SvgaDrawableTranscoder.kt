package com.zipper.magicimage.svga.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder

/**
 * 将数据转化为Drawable
 */
class SvgaDrawableTranscoder : ResourceTranscoder<SvgaFile, SvgaDrawableWrap> {
    override fun transcode(toTranscode: Resource<SvgaFile>, options: Options): Resource<SvgaDrawableWrap>? {
        val svgaFile = toTranscode.get()
        return SvgaResource(SvgaDrawableWrap(svgaFile))
    }
}