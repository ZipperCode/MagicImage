package com.zipper.magicimage.pag.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.zipper.magicimage.pag.glide.PagHelper.isPag
import org.libpag.PAGFile
import java.io.File

/**
 * from file load PagFile
 */
class PagFileDecoder : ResourceDecoder<File, PAGFile> {
    override fun handles(source: File, options: Options): Boolean {
        return source.inputStream().use {
            it.isPag()
        }
    }

    override fun decode(source: File, width: Int, height: Int, options: Options): Resource<PAGFile>? {
        val bytes = source.inputStream().readBytes()
        return SimpleResource(PAGFile.Load(bytes))
    }
}