package com.zipper.magicimage.pag.glide

import com.bumptech.glide.load.resource.drawable.DrawableResource

class PagFileResource(
    drawable: PagDrawable
) : DrawableResource<PagDrawable>(drawable) {
    override fun getResourceClass(): Class<PagDrawable> = PagDrawable::class.java

    override fun getSize(): Int = 1

    override fun recycle() {

    }
}