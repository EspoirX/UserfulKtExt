package com.lzx.userfulktext.ext.format

import android.graphics.Color
import android.view.Gravity
import java.math.BigDecimal
import java.util.*
import kotlin.math.roundToInt

/**
 * 单位转换
 * number 转换的数值
 * scale 保留多少位小数
 * threshold 临界值，大于该值的时候格式化
 * formatUnit 格式化单位
 * isKeepZero 整数的时候是否保留 .0
 * unit 转换的单位
 */
fun formatUnit(
    number: Double,
    scale: Int,
    threshold: Int,
    formatUnit: Int,
    isKeepZero: Boolean,
    unit: String
): String {
    var result: String
    if (number > threshold) {
        val num = number / formatUnit
        val decimal = BigDecimal(num)
        val value = decimal.setScale(scale, BigDecimal.ROUND_HALF_UP).toDouble()
        result = value.toString()
        if (!isKeepZero) {
            if (result.endsWith(".0")) {
                result = result.substring(0, result.length - 2)
            }
        }
        result += unit
    } else {
        result = number.toString()
        if (!isKeepZero) {
            if (result.endsWith(".0")) {
                result = result.substring(0, result.length - 2)
            }
        }
    }
    return result
}

/**
 * 转换成 00:00 格式，单位秒
 */
fun Long.formatSeconds(): String {
    return when {
        this <= 0 -> {
            "00:00"
        }
        this < 60 -> {
            String.format(Locale.getDefault(), "00:%02d", this % 60)
        }
        this < 3600 -> {
            String.format(Locale.getDefault(), "%02d:%02d", this / 60, this % 60)
        }
        else -> {
            String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                this / 3600,
                this % 3600 / 60,
                this % 60
            )
        }
    }
}

/**
 * 转换成 00:00 格式
 */
fun Long.formatTime(): String {
    var time = ""
    val minute = this / 60000
    val seconds = this % 60000
    val second = (seconds.toInt() / 1000.toFloat()).roundToInt().toLong()
    if (minute < 10) {
        time += "0"
    }
    time += "$minute:"
    if (second < 10) {
        time += "0"
    }
    time += second
    return time
}

fun Long.convertTimeStamps(): String {
    val time: Long = (this - System.currentTimeMillis()) / 1000
    val hours = time / 3600
    val minute = (time - hours * 3600) / 60
    return "$hours 小时 $minute 分钟后"
}

/**
 * 超过长度显示后缀
 */
fun String.omitLength(maxLength: Int, startIndex: Int = 0, omit: String = "..."): String {
    return if (this.length > maxLength) this.substring(startIndex, maxLength) + omit else this
}

fun Long.formatSize(): String {
    require(this >= 0) { "Size must larger than 0." }

    val byte = this.toDouble()
    val kb = byte / 1024.0
    val mb = byte / 1024.0 / 1024.0
    val gb = byte / 1024.0 / 1024.0 / 1024.0
    val tb = byte / 1024.0 / 1024.0 / 1024.0 / 1024.0

    return when {
        tb >= 1 -> "${tb.decimal(2)} TB"
        gb >= 1 -> "${gb.decimal(2)} GB"
        mb >= 1 -> "${mb.decimal(2)} MB"
        kb >= 1 -> "${kb.decimal(2)} KB"
        else -> "${byte.decimal(2)} B"
    }
}

fun Double.decimal(digits: Int): Double {
    return this.toBigDecimal()
        .setScale(digits, BigDecimal.ROUND_HALF_UP)
        .toDouble()
}

infix fun Long.ratio(bottom: Long): Double {
    if (bottom <= 0) {
        return 0.0
    }
    val result = (this * 100.0).toBigDecimal()
        .divide((bottom * 1.0).toBigDecimal(), 2, BigDecimal.ROUND_HALF_UP)
    return result.toDouble()
}

fun String.parseColor(): Int {
    return try {
        Color.parseColor(this)
    } catch (ex: Exception) {
        ex.printStackTrace()
        Color.BLACK
    }
}

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

fun String.md5(): String = MD5.hexdigest(this)