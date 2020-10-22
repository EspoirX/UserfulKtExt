package com.lzx.userfulktext.ext.json

import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.getObj(value: String): JSONObject {
    return this.getJSONObject(value)
}

fun JSONObject.getArray(value: String): JSONArray {
    return this.getJSONArray(value)
}

fun JSONObject.getOrNull(key: String): Any? {
    return if (this.has(key)) {
        this[key]
    } else {
        null
    }
}

inline fun <reified T> JSONArray.getOrNull(index: Int): T? {
    return if (index > 0 && index < this.length() - 1) {
        this.getJSONObject(index) as T
    } else {
        null
    }
}

fun JSONArray.iterator(): Iterator<JSONObject> =
    (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()

inline fun <reified T> JSONArray.forEach(action: (T?) -> Unit) {
    (0 until length()).forEach { action(get(it) as? T) }
}


fun String.toJsonObj(): JSONObject {
    return JSONObject(this)
}

fun String.toJsonArray(): JSONArray {
    return JSONArray(this)
}