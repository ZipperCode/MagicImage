package com.zipper.magicimage.pag.glide

import java.io.BufferedInputStream
import java.io.InputStream
import java.nio.ByteBuffer

object PagHelper {

    val HEADER = byteArrayOf(0x50, 0x41, 0x47)

    fun InputStream.isPag(): Boolean {
        val bytes = ByteArray(3)
        val buffer = BufferedInputStream(this)
        buffer.mark(3)
        buffer.read(bytes)
        buffer.reset()
        return bytes.contentEquals(HEADER)
    }

    fun ByteBuffer.isPag(): Boolean {
        this.rewind()
        val bytes = ByteArray(3)
        this.get(bytes)
        this.rewind()
        return bytes.contentEquals(HEADER)
    }
}