package com.qw73.fileManager.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorInt
import java.util.*

object Utils {
    private const val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm_"

    @ColorInt
    fun getColorAttribute(id: Int, context: Context): Int {
        val out = TypedValue()
        context.theme.resolveAttribute(id, out, true)
        return out.data
    }

    fun getRandomString(sizeOfRandomString: Int): String {
        val random = Random()
        val sb = StringBuilder(sizeOfRandomString)
        for (i in 0 until sizeOfRandomString) {
            sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        }
        return sb.toString()
    }
}