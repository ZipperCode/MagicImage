package com.zipper.magicimage.svga.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.zipper.magicimage.svga.glide.SvgaHelper.isSvga
import java.nio.ByteBuffer

/**
 * 从ByteBuffer中解码
 */
class SvgaByteBufferDecoder : ResourceDecoder<ByteBuffer, SvgaFile> {
    override fun handles(source: ByteBuffer, options: Options): Boolean = source.isSvga()

    override fun decode(source: ByteBuffer, width: Int, height: Int, options: Options): Resource<SvgaFile>? {
        val data = ByteArray(source.limit())
        source.rewind()
        source.get(data)
        source.rewind()
        val movieEntity = SvgaHelper.decodeMovieEntity(data)
        return SimpleResource(SvgaFile(movieEntity, width, height))
    }
}