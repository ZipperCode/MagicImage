package com.zipper.magicimage.pag.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.zipper.magicimage.pag.glide.PagHelper.isPag
import org.libpag.PAGFile
import java.nio.ByteBuffer

/**
 * from byteBuffer load a pag file
 */
class PagByteBufferDecoder : ResourceDecoder<ByteBuffer, PAGFile> {
    override fun
            handles(source: ByteBuffer, options: Options): Boolean = source.isPag()

    override fun decode(source: ByteBuffer, width: Int, height: Int, options: Options): Resource<PAGFile> {
        val data = ByteArray(source.limit())
        source.rewind()
        source.get(data)
        source.rewind()
        return SimpleResource(PAGFile.Load(data))
    }
}