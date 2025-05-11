package com.zipper.magicimage

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.zipper.magicimage.gif.MagicGifViewProvider
import java.util.concurrent.CopyOnWriteArrayList

object MagicRegistry {

    private val providerEntries = CopyOnWriteArrayList<Entry<*, *>>()

    init {
        append(GifDrawable::class.java, MagicGifViewProvider())
    }

    fun <T : Drawable, R : MagicView> append(drawableClass: Class<T>, provider: MagicViewProvider<R>) {
        providerEntries.add(Entry(drawableClass, provider))
    }

    fun <T : Drawable, R : MagicView> prepend(drawableClass: Class<T>, provider: MagicViewProvider<R>) {
        providerEntries.add(0, Entry(drawableClass, provider))
    }

    fun findProvider(drawableClass: Class<Drawable>): MagicViewProvider<*>? {
        for (entry in providerEntries) {
            if (entry.handles(drawableClass)) {
                return entry.provider
            }
        }
        return null
    }

    data class Entry<T : Drawable, R : MagicView>(
        val drawableClass: Class<T>,
        val provider: MagicViewProvider<R>
    ) {

        fun handles(drawableClass: Class<Drawable>): Boolean {
            return this.drawableClass.isAssignableFrom(drawableClass)
        }
    }
}