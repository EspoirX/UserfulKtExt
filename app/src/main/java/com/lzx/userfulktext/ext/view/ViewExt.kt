package com.lzx.userfulktext.ext.view

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lzx.userfulktext.ext.display.dp


fun View?.visible() {
    this?.visibility = View.VISIBLE
}

fun View?.gone() {
    this?.visibility = View.GONE
}

fun View?.visibilityBy(isVisible: Boolean) {
    this?.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun View?.isVisible() = this?.visibility == View.VISIBLE

fun View?.isGone() = this?.visibility == View.GONE

fun View?.isInvisible() = this?.visibility == View.INVISIBLE

fun View?.halfWidth(defaultWidth: Int = 0): Int {
    val halfWidth = this?.measuredWidth ?: 0 / 2
    return if (halfWidth == 0) defaultWidth.dp / 2 else halfWidth
}

fun View?.halfHeight(defaultHeight: Int = 0): Int {
    val halfHeight = this?.measuredHeight ?: 0 / 2
    return if (halfHeight == 0) defaultHeight.dp / 2 else halfHeight
}

/**
 * 获取基于 targetView 的左右居中 x 值
 */
fun View?.centerXbyTargetView(
    targetView: View?,
    targetViewDefaultWidth: Int = 0,
    thisViewDefaultWidth: Int = 0
): Int {
    return targetView?.left ?: 0 +
    targetView.halfWidth(targetViewDefaultWidth) - this.halfWidth(thisViewDefaultWidth)
}

/**
 * 获取基于 targetView 的左右居中 y 值
 */
fun View?.centerYbyTargetView(
    targetView: View?,
    targetViewDefaultHeight: Int = 0,
    thisViewDefaultHeight: Int = 0
): Int {
    return targetView?.top ?: 0 +
    targetView.halfHeight(targetViewDefaultHeight) - this.halfHeight(thisViewDefaultHeight)
}

fun View?.centerX(defaultWidth: Int = 0): Int {
    return this?.left ?: 0 + this.halfWidth(defaultWidth)
}

fun View?.centerY(defaultHeight: Int = 0): Int {
    return this?.top ?: 0 + this.halfHeight(defaultHeight)
}

fun ViewGroup?.childVisible(vararg views: View) {
    for (view in views) {
        view.visible()
    }
}

fun ViewGroup?.childGone(vararg views: View) {
    for (view in views) {
        view.gone()
    }
}

fun ViewGroup?.allChildGone() {
    this?.apply {
        for (i in 0..this.childCount) {
            this.getChildAt(i).gone()
        }
    }
}

fun ViewGroup?.allChildVisible() {
    this?.apply {
        for (i in 0..this.childCount) {
            this.getChildAt(i).visible()
        }
    }
}

fun ViewGroup.getChild(index: Int): View {
    return if (childCount > index) getChildAt(index) else getChildAt(0)
}

fun <T : View> T.waitForHeight(block: T.() -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) {
        this.block()
        return
    }
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        var lastHeight: Int? = null

        override fun onGlobalLayout() {
            if (lastHeight != null && lastHeight == measuredHeight) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                return
            }
            if (measuredWidth > 0 && measuredHeight > 0 && lastHeight != measuredHeight) {
                lastHeight = measuredHeight
                this@waitForHeight.block()
            }
        }
    })
}

fun View?.setMargins(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
    requestLayout: Boolean = false
) {
    if (this == null) return
    if (this.layoutParams is MarginLayoutParams) {
        (this.layoutParams as MarginLayoutParams).setMargins(left, top, right, bottom)
        if (requestLayout) {
            this.requestLayout()
        }
    }
}

fun View?.setUpLayoutParams(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.MATCH_PARENT
) {
    if (this?.parent == null) return
    when (this.parent) {
        is LinearLayout -> {
            this.layoutParams = LinearLayout.LayoutParams(width, height)
        }
        is RelativeLayout -> {
            this.layoutParams = RelativeLayout.LayoutParams(width, height)
        }
        is FrameLayout -> {
            this.layoutParams = FrameLayout.LayoutParams(width, height)
        }
        is ConstraintLayout -> {
            this.layoutParams = ConstraintLayout.LayoutParams(width, height)
        }
        else -> {
            this.layoutParams = ViewGroup.LayoutParams(width, height)
        }
    }
    requestLayout()
}

fun View?.changeSize(width: Int = -1, height: Int = -1) {
    if (width != -1) {
        this?.layoutParams?.width = width
    }
    if (height != -1) {
        this?.layoutParams?.height = height
    }
}

fun Window.setUpHeightPercent(value: Any) {
    var height = when (value) {
        is Int -> value.toFloat()
        is Float -> value
        else -> return
    }
    val windowHeight = this.windowManager.defaultDisplay.height
    val params = this.attributes
    when (height) {
        -1F -> params.height = windowHeight
        -2F -> params.height = windowHeight / 2
        else -> {
            if (height > 100) height = 100F
            height /= 100F
            height *= windowHeight.toFloat()
            params.height = height.toInt()
        }
    }
}

fun Window.setUpWidthPercent(value: Any) {
    var width = when (value) {
        is Int -> value.toFloat()
        is Float -> value
        else -> return
    }
    val windowWidth = this.windowManager.defaultDisplay.width
    val params = this.attributes
    when (width) {
        -1F -> params.width = windowWidth
        -2F -> params.width = windowWidth / 2
        else -> {
            if (width > 100) width = 100F
            width /= 100F
            width *= windowWidth.toFloat()
            params.width = width.toInt()
        }
    }
}

fun WindowManager.getWidthAndHeight(): Pair<Int, Int> {
    return Point()
        .apply { defaultDisplay.getSize(this) }
        .let { Pair(it.x, it.y) }
}

internal fun View.showKeyboard(delayMillis: Long = 0) {
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    if (delayMillis != 0L) {
        postDelayed({ imm?.showSoftInput(this, 0) }, delayMillis)
    } else {
        imm?.showSoftInput(this, 0)
    }
}

internal fun View.hideKeyboard() {
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    this.windowToken?.let {
        imm?.hideSoftInputFromWindow(it, 0)
    }
}

fun Int.getLayoutInflater(
    context: Context?,
    root: ViewGroup? = null,
    attachToRoot: Boolean = false
): View? = LayoutInflater.from(context).inflate(this, root, attachToRoot)

fun String.toGravity() = when (this) {
    "top",
    "TOP" -> {
        Gravity.TOP
    }
    "bottom",
    "BOTTOM" -> {
        Gravity.BOTTOM
    }
    "left",
    "LEFT" -> {
        Gravity.LEFT
    }
    "right",
    "RIGHT" -> {
        Gravity.RIGHT
    }
    "center",
    "CENTER" -> {
        Gravity.CENTER
    }
    else -> Gravity.CENTER
}

/**
 * 开始帧动画
 */
fun ImageView.startAnimationSafe() {
    drawable?.run {
        if (this is AnimationDrawable) {
            this.start()
        }
    }
}

/**
 * 结束动画
 */
fun ImageView.stopAnimationSafe() {
    drawable?.run {
        if (this is AnimationDrawable) {
            this.stop()
        }
    }
    clearAnimation()
}


fun ImageView?.loadImage(
    url: String?,
    placeholderResId: Int = 0,
    errorResId: Int = 0,
    targetHeight: Int = 0,            //加载大小
    targetWidth: Int = 0,             //加载大小
    dontAnimate: Boolean = false,     //是否需要动画
    skipLocalCache: Boolean = false,  //跳过本地缓存
    skipMemoryCache: Boolean = false, //跳过内存缓存
    isCenterCrop: Boolean = false,    //圆形
    roundAngle: Int = 0,              //圆角
    loadBlurImage: Boolean = false,   //高斯模糊
    blurRadius: Int = 0,               //模糊半径
    callback: DrawableCallBack.SimpleDrawableCallback? = null //回调
) {
    if (this?.context == null || url.isNullOrEmpty()) {
        return
    }
    Glide.with(this.context)
        .load(url)
        .placeholder(placeholderResId)
        .error(errorResId)
        .apply {
            if (targetHeight > 0 && targetWidth > 0) {
                override(targetWidth, targetHeight)
            }
            if (dontAnimate) {
                dontAnimate()
            }
            if (skipLocalCache) {
                diskCacheStrategy(DiskCacheStrategy.NONE)
            }
            if (skipMemoryCache) {
                skipMemoryCache(true)
            }
            if (isCenterCrop) {
                circleCrop()
            }
            if (roundAngle > 0) {
                transform(CenterCrop(), RoundedCorners(roundAngle))
            }
            if (loadBlurImage) {
                if (blurRadius > 0) {
                    transform(CenterCrop(), BlurTransformation(blurRadius))
                } else {
                    transform(CenterCrop(), BlurTransformation())
                }
            }
        }.apply {
            if (callback != null) {
                into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        callback.onDrawableLoaded(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        callback.onDrawableFailed(Exception("Glide onLoadFailed"), errorDrawable)
                    }
                })
            } else {
                into(this@loadImage)
            }
        }
}


interface DrawableCallBack {
    fun onDrawableLoaded(drawable: Drawable?)
    fun onDrawableFailed(e: Exception?, errorDrawable: Drawable?)
    abstract class SimpleDrawableCallback : DrawableCallBack {
        override fun onDrawableLoaded(drawable: Drawable?) {}
        override fun onDrawableFailed(e: Exception?, errorDrawable: Drawable?) {}
    }
}
