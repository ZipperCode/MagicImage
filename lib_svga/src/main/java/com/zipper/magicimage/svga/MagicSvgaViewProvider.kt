package com.zipper.magicimage.svga

import android.widget.ImageView
import com.zipper.magicimage.MagicParam
import com.zipper.magicimage.MagicView
import com.zipper.magicimage.MagicViewProvider

class MagicSvgaViewProvider : MagicViewProvider<MagicSvgaView> {
    override fun isAssignable(magicView: MagicView?): Boolean {
        return magicView is MagicSvgaView
    }

    override fun create(internalImageView: ImageView, param: MagicParam): MagicSvgaView {
        return MagicSvgaView(internalImageView.context, param)
    }
}