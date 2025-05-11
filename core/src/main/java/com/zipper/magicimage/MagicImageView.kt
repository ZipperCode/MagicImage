package com.zipper.magicimage

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.MainThread
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.transition.Transition
import com.zipper.magicimage.core.R

/**
 * 魔法ImageView
 * 适配特殊的View，如
 */
class MagicImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), MagicViewTarget {

    companion object {
        private val sScaleTypeArray: Array<ScaleType> = arrayOf(
            ScaleType.MATRIX,
            ScaleType.FIT_XY,
            ScaleType.FIT_START,
            ScaleType.FIT_CENTER,
            ScaleType.FIT_END,
            ScaleType.CENTER,
            ScaleType.CENTER_CROP,
            ScaleType.CENTER_INSIDE
        )
    }

    /**
     * [CustomViewTarget]
     */
    private val sizeDeterminer = SizeDeterminer(this)

    /**
     * 内部ImageView，用于普通图片以占位图使用，实际使用到MagicView之后会进行隐藏
     */
    private val internalImageView = ImageView(context)
    private var magicView: MagicView? = null

    private var request: Request? = null

    /**
     * 共享参数，让MagicView也能拿到
     */
    private val magicParam = MagicParam()

    /**
     * 动画回调
     */
    private val animateListener = mutableListOf<(IMagicAnimateIntent) -> Unit>()

    /**
     * 可能需要拿到MagicView的情况，通过回调，让外部拿到MagicView
     * 只有[onResourceReady]的时候才会创建MagicView，所以如果明确知道需要创建
     * 某个MagicView，则可以先调用[preInitMagicView]先初始化
     *
     * 加载到资源之后，会根据资源重新创建[MagicView]确保资源是匹配的，否则会覆盖新的MagicView
     */
    var onMagicViewAttachView: (MagicView) -> Unit = {}

    init {
        val it = context.obtainStyledAttributes(attrs, R.styleable.MagicImageView)
        val scaleType = it.getInt(R.styleable.MagicImageView_magicCompatScaleType, -1)
        if (scaleType in sScaleTypeArray.indices) {
            magicParam.scaleType = sScaleTypeArray[scaleType]
            internalImageView.scaleType = magicParam.scaleType
        }
        magicParam.animateRepeatCount = it.getInt(R.styleable.MagicImageView_magicAnimateRepeat, -1)

        val src = it.getResourceId(R.styleable.MagicImageView_magicCompatSrc, 0)
        if (src != 0) {
            internalImageView.setImageResource(src)
        }
        magicParam.usedInRecyclerView = it.getBoolean(R.styleable.MagicImageView_magicInRecyclerView, false)
        magicParam.autoAnim = it.getBoolean(R.styleable.MagicImageView_magicAutoAnim, true)
        magicParam.compatViewSize = it.getBoolean(R.styleable.MagicImageView_magicCompatViewSize, false)
        val enableListener = it.getBoolean(R.styleable.MagicImageView_magicAnimateListener, false)
        if (enableListener) {
            // 动画分发回调
            magicParam.animateIntentCallback = { intent ->
                post {
                    animateListener.forEach {
                        it(intent)
                    }
                }
            }
        }

        it.recycle()

        addView(internalImageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // 在RecyclerView中使用的时候，需要管理Glide的请求
        if (magicParam.usedInRecyclerView) {
            if (this.request?.isRunning == false) {
                this.request?.begin()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (magicParam.usedInRecyclerView) {
            this.request?.clear()
        }
    }

    override fun getView(): View = this

    override fun getScaleType(): ScaleType = magicParam.scaleType

    override fun onStart() {
        magicView?.processIntent(OnStartIntent)
    }

    override fun onStop() {
        magicView?.processIntent(OnStopIntent)
    }

    override fun onDestroy() = Unit

    override fun onLoadStarted(placeholder: Drawable?) {
        internalImageView.setImageDrawable(placeholder)
        internalImageView.visibility = VISIBLE
        magicView?.processIntent(OnLoadStartedIntent)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        internalImageView.setImageDrawable(errorDrawable)
        internalImageView.visibility = VISIBLE
        magicView?.processIntent(OnLoadFailedIntent)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        sizeDeterminer.clearCallbacksAndListener()
        internalImageView.setImageDrawable(placeholder)
        magicView?.processIntent(OnLoadClearedIntent)
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        compatScaleType(resource)
        preInitMagicView(resource.javaClass)
        if (magicView?.onResourceReady(resource) == true) {
            internalImageView.visibility = GONE
            return
        }
        internalImageView.setImageDrawable(resource)
        internalImageView.visibility = VISIBLE
    }

    override fun getSize(cb: SizeReadyCallback) {
        sizeDeterminer.getSize(cb)
    }

    override fun removeCallback(cb: SizeReadyCallback) {
        sizeDeterminer.removeCallback(cb)
    }

    override fun setRequest(request: Request?) {
        this.request = request
    }

    override fun getRequest(): Request? = request

    @MainThread
    fun addAnimateListener(listener: (IMagicAnimateIntent) -> Unit) {
        animateListener.add(listener)
    }

    @MainThread
    fun removeAnimateListener(listener: (IMagicAnimateIntent) -> Unit) {
        animateListener.remove(listener)
    }

    /**
     * 因为对应的扩展View需要加载资源之后才会初始化
     * 在资源未加载之前，如果需要拿到MagicView，则需要提前初始化
     */
    fun preInitMagicView(resourceType: Class<Drawable>) {
        val provider = MagicRegistry.findProvider(resourceType)
        if (provider?.isAssignable(magicView) == false) {
            magicView = provider.create(internalImageView, magicParam)
            val realView = magicView?.getView()
            val existsView = findViewById<View?>(R.id.magic_view_id)
            if (existsView != null) {
                removeView(existsView)
            }
            addView(realView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            onMagicViewAttachView(magicView!!)
        }
    }

    fun setScaleType(scaleType: ScaleType) {
        magicParam.scaleType = scaleType
        internalImageView.scaleType = magicParam.scaleType
        magicView?.setScaleType(scaleType)
    }

    fun setRepeatCount(count: Int) {
        magicParam.animateRepeatCount = count
        magicView?.processIntent(RepeatCount(count))
    }

    private fun compatScaleType(resource: Drawable) {
        val drawableWidth = resource.intrinsicWidth
        val drawableHeight = resource.intrinsicHeight
        // 不需要做适配处理
        if (!magicParam.compatViewSize || drawableWidth <= 0 && drawableHeight <= 0) {
            return
        }
        val lp = layoutParams
        when (magicParam.scaleType) {
            ScaleType.FIT_XY -> {
                if (lp.width.isWrapContent() && !lp.height.isWrapContent()) {
                    // 宽度自适应
                    val viewHeight = height

                    val width = (drawableWidth * 1f * viewHeight / drawableHeight).toInt()
                    lp.width = width
                    setLayoutParams(lp)
                } else if (lp.height.isWrapContent() && !lp.width.isWrapContent()) {
                    // 高度自适应
                    val viewWidth = width
                    val height = (drawableHeight * 1f * viewWidth / drawableWidth).toInt()
                    lp.height = height
                    layoutParams = lp
                }
            }

            else -> {
                // 自适应
                if (lp.width.isWrapContent() && lp.height.isWrapContent()) {
                    val size = SizeDeterminer.getDisplaySize(context)
                    val width = minOf(drawableWidth, size.x)
                    val height = minOf(drawableHeight, size.y)
                    lp.width = width
                    lp.height = height
                    layoutParams = lp
                }
            }
        }
    }

    private fun Int.isWrapContent(): Boolean {
        return this == LayoutParams.WRAP_CONTENT
    }
}