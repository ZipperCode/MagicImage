package com.zipper.magicimage.svga.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.zipper.magicimage.svga.glide.SvgaHelper.isSvga
import java.io.InputStream

class SvgaStreamDecoder : ResourceDecoder<InputStream, SvgaFile> {
    override fun handles(source: InputStream, options: Options): Boolean = source.isSvga()

    override fun decode(source: InputStream, width: Int, height: Int, options: Options): Resource<SvgaFile> {
        val movieEntity = SvgaHelper.decodeMovieEntity(source)
        return SimpleResource(SvgaFile(movieEntity, width, height))
    }
}