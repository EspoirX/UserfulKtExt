package com.lzx.userfulktext.ext.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * Activity 相关
 *
 *       navigationTo<UserInfoActivity>(
 *           "userId" to 1,
 *          "userName" to "ABC")
 */
inline fun <reified T : Activity> Context.navigationTo(vararg params: Pair<String, Any?>) =
    internalStartActivity(this, T::class.java, params)

inline fun <reified T : Activity> Activity.navigationTo(vararg params: Pair<String, Any?>) =
    internalStartActivity(this, T::class.java, params)

inline fun <reified T : Activity> androidx.fragment.app.Fragment.navigationTo(vararg params: Pair<String, Any?>) =
    context?.let { internalStartActivity(it, T::class.java, params) }

inline fun <reified T : Activity> Activity.navigationToForResult(requestCode: Int, vararg params: Pair<String, Any?>) =
    internalStartActivityForResult(this, T::class.java, requestCode, params)

fun Activity.finishForResult(vararg params: Pair<String, Any?>) {
    setResult(Activity.RESULT_OK, createIntent<Activity>(params = params))
    finish()
}

fun internalStartActivity(
    ctx: Context,
    activity: Class<out Activity>,
    params: Array<out Pair<String, Any?>>
) {
    ctx.startActivity(createIntent(ctx, activity, params))
}

fun internalStartActivityForResult(
    act: Activity,
    activity: Class<out Activity>,
    requestCode: Int,
    params: Array<out Pair<String, Any?>>
) {
    act.startActivityForResult(createIntent(act, activity, params), requestCode)
}

fun <T> createIntent(ctx: Context? = null, clazz: Class<out T>? = null, params: Array<out Pair<String, Any?>>): Intent {
    val intent = if (clazz == null) Intent() else Intent(ctx, clazz)
    if (params.isNotEmpty()) fillIntentArguments(intent, params)
    return intent
}

private fun fillIntentArguments(intent: Intent, params: Array<out Pair<String, Any?>>) {
    params.forEach {
        when (val value = it.second) {
            null -> intent.putExtra(it.first, null as Serializable?)
            is Int -> intent.putExtra(it.first, value)
            is Long -> intent.putExtra(it.first, value)
            is CharSequence -> intent.putExtra(it.first, value)
            is String -> intent.putExtra(it.first, value)
            is Float -> intent.putExtra(it.first, value)
            is Double -> intent.putExtra(it.first, value)
            is Char -> intent.putExtra(it.first, value)
            is Short -> intent.putExtra(it.first, value)
            is Boolean -> intent.putExtra(it.first, value)
            is Serializable -> intent.putExtra(it.first, value)
            is Bundle -> intent.putExtra(it.first, value)
            is Parcelable -> intent.putExtra(it.first, value)
            is Array<*> -> when {
                value.isArrayOf<CharSequence>() -> intent.putExtra(it.first, value)
                value.isArrayOf<String>() -> intent.putExtra(it.first, value)
                value.isArrayOf<Parcelable>() -> intent.putExtra(it.first, value)
                else -> throw RuntimeException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            is IntArray -> intent.putExtra(it.first, value)
            is LongArray -> intent.putExtra(it.first, value)
            is FloatArray -> intent.putExtra(it.first, value)
            is DoubleArray -> intent.putExtra(it.first, value)
            is CharArray -> intent.putExtra(it.first, value)
            is ShortArray -> intent.putExtra(it.first, value)
            is BooleanArray -> intent.putExtra(it.first, value)
            else -> throw RuntimeException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
        }
        return@forEach
    }
}

fun Context?.isContextValid(): Boolean {
    if (this == null) return false
    if (this !is Activity) {
        return false
    }
    return this.isActivityValid()
}

fun Activity.isActivityValid(): Boolean {
    if (this.isFinishing) {
        return false
    }
    if (Build.VERSION.SDK_INT >= 17) {
        return !this.isDestroyed
    }
    return true
}