package com.lzx.userfulktext.ext.display

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

val Int.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

val Float.px
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this,
        Resources.getSystem().displayMetrics
    )

val Int.px
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

val Float.pt
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PT,
        this,
        Resources.getSystem().displayMetrics
    )

val Int.pt
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PT,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

fun Context.getPhoneWidth(): Int {
    return this.resources.displayMetrics.widthPixels
}

fun Context.getPhoneHeight(): Int {
    return this.resources.displayMetrics.heightPixels
}

/**
 * 状态栏高度
 */
fun Context.getStatusBarHeight(): Int {
    val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
    return this.resources.getDimensionPixelSize(resourceId)
}