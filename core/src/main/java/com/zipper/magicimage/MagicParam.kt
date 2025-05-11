package com.zipper.magicimage

import android.widget.ImageView
import android.widget.ImageView.ScaleType

class MagicParam {

    var scaleType: ScaleType = ScaleType.FIT_CENTER

    var animateRepeatCount: Int = -1

    var autoAnim = true

    var compatViewSize = false

    var usedInRecyclerView = false

    var animateIntentCallback: (IMagicAnimateIntent) -> Unit = {}
}