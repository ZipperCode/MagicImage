package com.zipper.magicimage.svga.glide

import com.opensource.svgaplayer.proto.MovieEntity

/**
 * svga文件描述
 */
data class SvgaFile(
    val movieEntity: MovieEntity,
    val width: Int,
    val height: Int,
    val cacheFilePath: String = ""
)