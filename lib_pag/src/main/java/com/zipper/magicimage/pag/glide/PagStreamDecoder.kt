package com.zipper.magicimage.pag.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.zipper.magicimage.pag.glide.PagHelper.isPag
import org.libpag.PAGFile
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * from stream load pag File
 */
class PagStreamDecoder : ResourceDecoder<InputStream, PAGFile> {
    override fun handles(source: InputStream, options: Options): Boolean = source.isPag()

    override fun decode(source: InputStream, width: Int, height: Int, options: Options): Resource<PAGFile> {
        ByteArrayOutputStream(source.available()).use {
            val buffer = ByteArray(8 * 1024)
            while (source.read(buffer) != -1) {
                it.write(buffer)
            }
            return SimpleResource(PAGFile.Load(it.toByteArray()))
        }
    }
}