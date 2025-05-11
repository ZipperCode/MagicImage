package com.zipper.magicimage.gif

import android.widget.ImageView
import com.zipper.magicimage.MagicParam
import com.zipper.magicimage.MagicView
import com.zipper.magicimage.MagicViewProvider

class MagicGifViewProvider : MagicViewProvider<MagicGifView> {
    override fun isAssignable(magicView: MagicView?): Boolean {
        if (magicView == null) {
            return false
        }
        return magicView is MagicGifView
    }

    override fun create(internalImageView: ImageView, param: MagicParam): MagicGifView {
        return MagicGifView(internalImageView, param)
    }
}