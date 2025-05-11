package com.zipper.magicimage

import android.widget.ImageView

interface MagicViewProvider<out R : MagicView> {
    /**
     * 判断Provider和MagicView类型是否匹配
     */
    fun isAssignable(magicView: MagicView?): Boolean

    /**
     * 创建View
     */
    fun create(internalImageView: ImageView, param: MagicParam): R
}