package com.zipper.magicimage

import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import com.bumptech.glide.request.target.SizeReadyCallback
import java.lang.ref.WeakReference

class SizeDeterminer(private val view: View) {

    companion object {

        const val PENDING_SIZE = 0

        private val displaySize = Point()

        private var maxDisplayLength: Int? = null

        internal fun getDisplaySize(context: Context): Point {
            if (displaySize.x == 0 || displaySize.y == 0) {
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                wm.defaultDisplay.getSize(displaySize)
            }
            return displaySize
        }

        private fun getMaxDisplayLength(context: Context): Int {
            if (maxDisplayLength == null) {
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val displayDimensions = Point()
                wm.defaultDisplay.getSize(displayDimensions)
                maxDisplayLength = maxOf(displayDimensions.x, displayDimensions.y)
            }
            return maxDisplayLength!!
        }

    }

    private val cbs = ArrayList<SizeReadyCallback>()
    private var waitForLayout = false

    private var layoutListener: SizeDeterminerLayoutListener? = null


    private fun notifyCbs(width: Int, height: Int) {
        val newList = ArrayList(cbs)
        for (cb in newList) {
            cb.onSizeReady(width, height)
        }
    }

    fun checkCurrentDimens() {
        if (cbs.isEmpty()) {
            return
        }
        val currentWidth = getTargetWidth()
        val currentHeight = getTargetHeight()
        if (!isViewStateAndSizeValid(currentWidth, currentHeight)) {
            return
        }
        notifyCbs(currentWidth, currentHeight)
        clearCallbacksAndListener()
    }

    fun getSize(cb: SizeReadyCallback) {
        val currentWidth = getTargetWidth()
        val currentHeight = getTargetHeight()
        if (isViewStateAndSizeValid(currentWidth, currentHeight)) {
            cb.onSizeReady(currentWidth, currentHeight)
            return
        }
        if (!cbs.contains(cb)) {
            cbs.add(cb)
        }
        if (layoutListener == null) {
            layoutListener = SizeDeterminerLayoutListener(this)
            view.viewTreeObserver.addOnPreDrawListener(layoutListener)
        }
    }

    internal fun removeCallback(cb: SizeReadyCallback) {
        cbs.remove(cb)
    }

    private fun isViewStateAndSizeValid(width: Int, height: Int): Boolean {
        return width.isDimensionValid() && height.isDimensionValid()
    }

    private fun getTargetWidth(): Int {
        val horizontalPadding = view.paddingLeft + view.paddingRight
        val layoutParamSize = view.layoutParams?.width ?: PENDING_SIZE
        return getTargetDimen(view.width, layoutParamSize, horizontalPadding)
    }

    private fun getTargetHeight(): Int {
        val verticalPadding = view.paddingTop + view.paddingBottom
        val layoutParamSize = view.layoutParams?.height ?: PENDING_SIZE
        return getTargetDimen(view.height, layoutParamSize, verticalPadding)
    }

    private fun getTargetDimen(viewSize: Int, paramSize: Int, paddingSize: Int): Int {
        val adjustParamSize = paramSize - paddingSize
        if (adjustParamSize > 0) {
            return adjustParamSize
        }
        if (waitForLayout && view.isLayoutRequested) {
            return PENDING_SIZE
        }

        val adjustViewSize = viewSize - paddingSize
        if (adjustViewSize > 0) {
            return adjustViewSize
        }
        if (!view.isLayoutRequested && paramSize == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return getMaxDisplayLength(view.context)
        }

        return PENDING_SIZE
    }

    private fun Int.isDimensionValid(): Boolean {
        return this > 0 || this == PENDING_SIZE
    }

    internal fun clearCallbacksAndListener() {
        val observer = view.viewTreeObserver
        if (observer.isAlive) {
            observer.removeOnPreDrawListener(layoutListener)
        }
        layoutListener = null
        cbs.clear()
    }

    private class SizeDeterminerLayoutListener(
        sizeDeterminer: SizeDeterminer
    ) : ViewTreeObserver.OnPreDrawListener {
        private val sizeDeterminerRef = WeakReference(sizeDeterminer)
        override fun onPreDraw(): Boolean {
            sizeDeterminerRef.get()?.checkCurrentDimens()
            return true
        }

    }
}