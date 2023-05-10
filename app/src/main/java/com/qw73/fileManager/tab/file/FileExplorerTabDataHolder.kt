package com.qw73.fileManager.tab.file

import android.os.Parcelable
import com.qw73.fileManager.tab.BaseDataHolder
import java.io.File

class FileExplorerTabDataHolder(override val tag: String) : BaseDataHolder() {
    @JvmField
    var activeDirectory: File? = null

    @JvmField
    var recyclerViewStates: HashMap<File, Parcelable> = HashMap()

    @JvmField
    var selectedFiles = ArrayList<File>()
}