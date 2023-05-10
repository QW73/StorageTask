package com.qw73.fileManager.tab.file.misc

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qw73.fileManager.activity.MainActivity
import com.qw73.fileManager.extension.openFileWith
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FileOpener(private val mainActivity: MainActivity) {
    fun openFile(file: File) {
        if (!handleKnownFileExtensions(file)) {
            file.openFileWith(false)
        }
    }

    private fun handleKnownFileExtensions(file: File): Boolean {

        if (file.extension == FileMimeTypes.apkType) {
            val dialog = MaterialAlertDialogBuilder(mainActivity)
                .setMessage("Do you want to install this app?")
                .setPositiveButton("Install") { _, _ -> file.openFileWith(false) }
            CoroutineScope(Dispatchers.Main).launch {
                dialog.setTitle(FileUtils.getApkName(file))
                dialog.setIcon(FileUtils.getApkIcon(file))
                dialog.show()
            }
            return true
        }
        return false
    }


    companion object {
        private val TAG = FileOpener::class.java.simpleName
    }
}