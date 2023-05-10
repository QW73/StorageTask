package com.qw73.fileManager.tab.file.misc

import com.qw73.fileManager.App
import com.qw73.fileManager.tab.file.misc.BuildUtils.unzipFromAssets
import java.io.File

object APKSignerUtils {
    val pk8: File
        get() {
            val check = File(App.appContext.filesDir.toString() + "/build/testkey.pk8")
            if (check.exists()) {
                return check
            }
            unzipFromAssets(
                App.appContext,
                "build/testkey.pk8.zip",
                check.parentFile?.absolutePath
            )
            return check
        }
    val pem: File
        get() {
            val check = File(App.appContext.filesDir.toString() + "/build/testkey.x509.pem")
            if (check.exists()) {
                return check
            }
            unzipFromAssets(
                App.appContext,
                "build/testkey.x509.pem.zip",
                check.parentFile?.absolutePath
            )
            return check
        }
}