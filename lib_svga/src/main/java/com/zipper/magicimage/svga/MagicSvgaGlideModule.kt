package com.zipper.magicimage.svga

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.LibraryGlideModule
import com.zipper.magicimage.MagicRegistry
import com.zipper.magicimage.svga.glide.SvgaByteBufferDecoder
import com.zipper.magicimage.svga.glide.SvgaDrawableTranscoder
import com.zipper.magicimage.svga.glide.SvgaDrawableWrap
import com.zipper.magicimage.svga.glide.SvgaFile
import com.zipper.magicimage.svga.glide.SvgaStreamDecoder
import java.io.InputStream
import java.nio.ByteBuffer

@GlideModule
class MagicSvgaGlideModule : LibraryGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry
            .append(InputStream::class.java, SvgaFile::class.java, SvgaStreamDecoder())
            .append(ByteBuffer::class.java, SvgaFile::class.java, SvgaByteBufferDecoder())
            .register(SvgaFile::class.java, SvgaDrawableWrap::class.java, SvgaDrawableTranscoder())

        MagicRegistry.append(SvgaDrawableWrap::class.java, MagicSvgaViewProvider())
    }
}