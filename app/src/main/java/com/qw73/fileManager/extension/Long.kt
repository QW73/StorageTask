package com.qw73.fileManager.extension

import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

fun Long.toFormattedSize(): String {
    if (this <= 0) return "0 байт"
    val units = arrayOf("байт", "Кб", "Мб", "Гб", "Тб")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(
        this / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}