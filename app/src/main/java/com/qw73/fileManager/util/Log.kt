package com.qw73.fileManager.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.qw73.fileManager.extension.surroundWithBrackets
import com.qw73.fileManager.tab.file.misc.FileUtils
import java.io.*
import java.text.SimpleDateFormat

object Log {
    private const val TAG = "CustomLog"
    const val UNABLE_TO = "Unable to"
    private var logFile: File? = null
    fun start(context: Context) {
        logFile = File(context.getExternalFilesDir(null)!!.absolutePath + "/debug/log.txt")
    }

    fun e(tag: String?, e: Exception?) {
        e(tag, "", e)
    }

    @JvmOverloads
    fun e(tag: String?, msg: String, throwable: Throwable? = null) {
        write(tag, "Error", msg, throwable)
    }

    fun w(tag: String?, e: Exception?) {
        w(tag, "", e)
    }

    @JvmOverloads
    fun w(tag: String?, msg: String, throwable: Throwable? = null) {
        write(tag, "Warning", msg, throwable)
    }

    fun d(tag: String?, e: Exception?) {
        d(tag, "", e)
    }

    @JvmOverloads
    fun d(tag: String?, msg: String, throwable: Throwable? = null) {
        write(tag, "Warning", msg, throwable)
    }

    fun i(tag: String?, e: Exception?) {
        i(tag, "", e)
    }

    @JvmOverloads
    fun i(tag: String?, msg: String, throwable: Throwable? = null) {
        write(tag, "Info", msg, throwable)
    }

    @get:SuppressLint("SimpleDateFormat")
    private val currentTime: String
        get() = SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(System.currentTimeMillis())

    fun getStackTrace(throwable: Throwable): String {
        val result: Writer = StringWriter()
        val printWriter = PrintWriter(result)
        throwable.printStackTrace(printWriter)
        val stacktraceAsString = result.toString()
        printWriter.close()
        return stacktraceAsString
    }

    private fun write(tag: String?, priority: String, msg: String, throwable: Throwable?) {
        if (logFile == null) return
        if (!logFile!!.parentFile?.exists()!! && !logFile!!.parentFile?.mkdirs()!!) {
            Log.e(TAG, UNABLE_TO + " " + FileUtils.CREATE_FILE + ": " + logFile!!.parentFile)
            return
        }
        try {
            if (!logFile!!.exists() && !logFile!!.createNewFile()) {
                Log.e(TAG, UNABLE_TO + " " + FileUtils.CREATE_FILE + ": " + logFile)
                return
            }
            val logToWrite = StringBuilder()
            if (logFile!!.length() > 0) logToWrite.append(System.lineSeparator())
            logToWrite.append(currentTime.surroundWithBrackets())
                .append(priority.surroundWithBrackets())
                .append(tag!!.surroundWithBrackets())
                .append(":").append(" ")
            if (msg.isNotEmpty()) {
                logToWrite.append(msg)
            }
            if (throwable != null) {
                logToWrite.append(System.lineSeparator()).append(getStackTrace(throwable))
            }
            val fileWriter = FileWriter(logFile, true)
            fileWriter.write(logToWrite.toString())
            fileWriter.flush()
            fileWriter.close()
        } catch (e: Exception) {
            Log.e(TAG, UNABLE_TO + " write to log file" + System.lineSeparator() + e)
            Log.e(tag, msg, throwable)
        }
    }

}