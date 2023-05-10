package com.qw73.fileManager.util

import com.pixplicity.easyprefs.library.Prefs

object PrefsUtils {
    const val SORT_NAME_A2Z = 1
    const val SORT_NAME_Z2A = 2
    const val SORT_SIZE_SMALLER = 3
    const val SORT_SIZE_BIGGER = 4
    const val SORT_DATE_EXTENSION = 5
    const val SORT_DATE_NEWER = 6

    object FileExplorerTab {
        @JvmStatic
        var sortingMethod: Int
            get() = Prefs.getInt("sorting_method", SORT_NAME_A2Z)
            set(method) {
                Prefs.putInt("sorting_method", method)
            }

        fun setListFoldersFirst(b: Boolean) {
            Prefs.putBoolean("list_folders_first", b)
        }

        @JvmStatic
        fun listFoldersFirst(): Boolean {
            return Prefs.getBoolean("list_folders_first", true)
        }
    }

}