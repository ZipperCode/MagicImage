package com.zipper.magicimage.pag

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.LibraryGlideModule
import com.zipper.magicimage.MagicRegistry
import com.zipper.magicimage.pag.glide.PagByteBufferDecoder
import com.zipper.magicimage.pag.glide.PagDrawable
import com.zipper.magicimage.pag.glide.PagDrawableTranscoder
import com.zipper.magicimage.pag.glide.PagFileDecoder
import com.zipper.magicimage.pag.glide.PagStreamDecoder
import org.libpag.PAGFile
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer

@GlideModule
class MagicPagGlideModule : LibraryGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry
            .prepend(InputStream::class.java, PAGFile::class.java, PagStreamDecoder())
            .prepend(ByteBuffer::class.java, PAGFile::class.java, PagByteBufferDecoder())
            .prepend(File::class.java, PAGFile::class.java, PagFileDecoder())
            .register(PAGFile::class.java, PagDrawable::class.java, PagDrawableTranscoder())

        MagicRegistry.append(PagDrawable::class.java, MagicPagViewProvider())
    }
}