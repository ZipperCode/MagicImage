package com.zipper.magicimage.pag

import android.widget.ImageView
import com.zipper.magicimage.MagicParam
import com.zipper.magicimage.MagicView
import com.zipper.magicimage.MagicViewProvider

class MagicPagViewProvider : MagicViewProvider<MagicPagView> {

    override fun isAssignable(magicView: MagicView?): Boolean {
        if (magicView == null) {
            return false
        }
        return magicView is MagicPagView
    }

    override fun create(internalImageView: ImageView, param: MagicParam): MagicPagView {
        return MagicPagView(internalImageView.context, param)
    }
}