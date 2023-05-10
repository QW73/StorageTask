package com.qw73.fileManager.extension

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.qw73.fileManager.App
import com.qw73.fileManager.App.Companion.showMsg
import com.qw73.fileManager.tab.file.misc.FileMimeTypes
import java.io.File
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

fun File.getFileDetails(): String {
    val sb = StringBuilder()
    sb.append(getLastModifiedDate())
    sb.append("  |  ")
    if (this.isFile) {
        sb.append(length().toFormattedSize())
    } else {
        sb.append(getFormattedFileCount())
    }
    sb.append("  |  ")
    sb.append(getFolderSize().toFormattedSize())
    return sb.toString()
}

fun File.isExternalStorageFolder(): Boolean {
    return this.absolutePath == Environment.getExternalStorageDirectory().absolutePath
}

fun File.getFormattedFileCount(): String {
    val noItemsString = "пусто"
    if (this.isFile) {
        return noItemsString
    }
    var files = 0
    var folders = 0
    val fileList = this.listFiles() ?: return noItemsString
    for (item in fileList) {
        if (item.isFile) files++ else folders++
    }
    val sb = java.lang.StringBuilder()
    if (folders > 0) {
        sb.append("папки (")
        sb.append(folders)
        sb.append(")")
        if (files > 0) sb.append(", ")
    }
    if (files > 0) {
        sb.append("файлы (")
        sb.append(files)
        sb.append(")")
    }
    return if (folders == 0 && files == 0) noItemsString else sb.toString()
}

fun File.getMimeTypeFromFile(): String {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        ?: getMimeTypeFromExtension()
}

fun File.getMimeTypeFromExtension(): String {
    val type = FileMimeTypes.mimeTypes[extension]
    return type ?: FileMimeTypes.default
}

fun File.openFileWith(anonymous: Boolean) {
    val i = Intent(Intent.ACTION_VIEW)
    val uri = FileProvider.getUriForFile(
        App.appContext, App.appContext.packageName + ".provider",
        this
    )
    i.setDataAndType(uri, if (anonymous) "*/*" else getMimeTypeFromFile())
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    try {
        App.appContext.startActivity(i)
    } catch (e: ActivityNotFoundException) {
        if (!anonymous) {
            openFileWith(true)
        } else {
            showMsg("Couldn't find any app that can open this type of files")
        }
    } catch (e: Exception) {
        //Log.i(TAG, e);
        showMsg("Failed to open this file")
    }
}

fun File.getFolderSize(): Long {
    var size: Long = 0
    val list = listFiles()
    if (list != null) {
        for (child in list) {
            size = if (child.isFile) {
                size + child.length()
            } else {
                size + child.getFolderSize()
            }
        }
    }
    return size
}

@SuppressLint("SimpleDateFormat")
fun File.getLastModifiedDate(REGULAR_DATE_FORMAT: String = "dd MMMM yyyy, HH:mm"): String {
    val formatSymbols = DateFormatSymbols(Locale("ru"))
    val dateFormat = SimpleDateFormat(REGULAR_DATE_FORMAT, formatSymbols)
    return dateFormat.format(Date(lastModified()))
}