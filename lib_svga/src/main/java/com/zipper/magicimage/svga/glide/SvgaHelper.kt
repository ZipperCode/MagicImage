package com.zipper.magicimage.svga.glide

import com.bumptech.glide.load.Option
import com.opensource.svgaplayer.proto.MovieEntity
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.zip.Deflater
import java.util.zip.Inflater

object SvgaHelper {

    internal val modelOption = Option.memory<String>("kids_image_file_model_key")

    fun String.isSvga(): Boolean {
        try {
            FileInputStream(this).use { fis ->
                val buf = ByteArray(2)
                fis.read(buf)
                return buf.isSvga()
            }
        } catch (ignored: Exception) {
            return false
        }
    }

    fun InputStream.isSvga(): Boolean {
        try {
            val bis = BufferedInputStream(this)
            bis.mark(2)
            val buf = ByteArray(2)
            bis.read(buf)
            bis.reset()
            return buf.isSvga()
        } catch (e: IOException) {
            return false
        }
    }

    fun ByteArray.isSvga(): Boolean {
        if (size < 2) {
            return false
        }
        val magic = this[0].toInt() and 0xFF or ((this[1].toInt() and 0xFF) shl 8)
        return magic == 0x9C78
    }

    fun ByteBuffer.isSvga(): Boolean {
        rewind()
        val bytes = ByteArray(2)
        get(bytes)
        rewind()
        return bytes.isSvga()
    }

    @Throws(IOException::class)
    fun decodeMovieEntity(source: InputStream): MovieEntity {
        ByteArrayOutputStream().use { os ->
            source.copyTo(os)
            val bytes = os.toByteArray()
            val svgaSourceBytes = inflate(bytes)
            return MovieEntity.ADAPTER.decode(svgaSourceBytes)
        }
    }

    fun decodeMovieEntity(bytes: ByteArray) = MovieEntity.ADAPTER.decode(inflate(bytes))

    private fun inflate(byteArray: ByteArray): ByteArray {
        val inflater = Inflater()
        inflater.setInput(byteArray, 0, byteArray.size)
        val bufferSize = 8 * 1024
        val inflateBuffer = ByteArray(bufferSize)
        ByteArrayOutputStream().use { inflatedOutputStream ->
            while (true) {
                val count = inflater.inflate(inflateBuffer, 0, bufferSize)
                if (count <= 0) {
                    break
                } else {
                    inflatedOutputStream.write(inflateBuffer, 0, count)
                }
            }
            inflater.end()
            return inflatedOutputStream.toByteArray()
        }
    }

    @Throws(IOException::class)
    fun deflate(bytes: ByteArray?): ByteArray? {
        val deflater = Deflater()
        deflater.setInput(bytes)
        deflater.finish()
        val bufferSize = 8 * 1024
        val buffer = ByteArray(bufferSize)
        ByteArrayOutputStream().use { inflatedOutputStream ->
            while (true) {
                val count = deflater.deflate(buffer, 0, bufferSize)
                if (count <= 0) {
                    break
                } else {
                    inflatedOutputStream.write(buffer, 0, count)
                }
            }
            deflater.end()
            return inflatedOutputStream.toByteArray()
        }
    }
}