package com.lzx.userfulktext.ext.file

import java.io.File
import java.io.RandomAccessFile
import java.util.regex.Pattern

fun String.getFileNameFromUrl(): String {
    var temp = this
    if (temp.isNotEmpty()) {
        val fragment = temp.lastIndexOf('#')
        if (fragment > 0) {
            temp = temp.substring(0, fragment)
        }

        val query = temp.lastIndexOf('?')
        if (query > 0) {
            temp = temp.substring(0, query)
        }

        val filenamePos = temp.lastIndexOf('/')
        val filename = if (0 <= filenamePos) temp.substring(filenamePos + 1) else temp

        if (filename.isNotEmpty() && Pattern.matches("[a-zA-Z_0-9.\\-()%]+", filename)) {
            return filename
        }
    }

    return ""
}

fun File.shadow(): File {
    val shadowPath = "$canonicalPath.download"
    return File(shadowPath)
}

fun File.recreate(length: Long = 0L, block: () -> Unit = {}) {
    delete()
    val created = createNewFile()
    if (created) {
        setLength(length)
        block()
    } else {
        throw IllegalStateException("File create failed!")
    }
}

fun File.setLength(length: Long = 0L) {
    RandomAccessFile(this, "rw").setLength(length)
}