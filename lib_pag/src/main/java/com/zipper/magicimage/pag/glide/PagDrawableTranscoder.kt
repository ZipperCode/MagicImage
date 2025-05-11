package com.zipper.magicimage.pag.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import org.libpag.PAGFile

class PagDrawableTranscoder: ResourceTranscoder<PAGFile, PagDrawable> {
    override fun transcode(toTranscode: Resource<PAGFile>, options: Options): Resource<PagDrawable>? {
        return PagFileResource(PagDrawable(toTranscode.get()))
    }
}