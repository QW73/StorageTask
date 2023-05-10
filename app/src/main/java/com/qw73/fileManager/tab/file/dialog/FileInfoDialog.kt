package com.qw73.fileManager.tab.file.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.qw73.fileManager.R
import com.qw73.fileManager.extension.getFolderSize
import com.qw73.fileManager.extension.getFormattedFileCount
import com.qw73.fileManager.extension.getLastModifiedDate
import com.qw73.fileManager.extension.toFormattedSize
import com.qw73.fileManager.tab.file.misc.IconHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FileInfoDialog(private val file: File) : BottomSheetDialogFragment() {

    private val infoList = ArrayList<InfoHolder>()
    private var useDefaultFileInfo = false
    private lateinit var container: ViewGroup

    fun setUseDefaultFileInfo(`is`: Boolean): FileInfoDialog {
        useDefaultFileInfo = `is`
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.file_explorer_tab_info_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.findViewById<View>(R.id.file_name) as TextView).text = file.name
        IconHelper.setFileIcon(view.findViewById(R.id.file_icon), file)
        container = view.findViewById(R.id.container)
        if (!useDefaultFileInfo) {
            for (holder in infoList) {
                addItemView(holder, container)
            }
        } else {
            if (file.isFile) {
                addDefaultFileInfo()
            } else {
                addDefaultFolderInfo()
            }
        }
    }

    private fun addDefaultFolderInfo() {
        addItemView(InfoHolder("Путь:", file.absolutePath), container)
        addItemView(
            InfoHolder(
                "Изменен:", file.getLastModifiedDate()
            ), container
        )
        val count = addItemView(InfoHolder("Седержание:", "..."), container)
        CoroutineScope(Dispatchers.IO).launch {
            val s = file.getFormattedFileCount()
            withContext(Dispatchers.Main) { count.text = s }
        }.start()
        val size = addItemView(InfoHolder("Размер:", "..."), container)
        CoroutineScope(Dispatchers.IO).launch {
            val s = file.getFolderSize().toFormattedSize()
            withContext(Dispatchers.Main) { size.text = s }
        }.start()
    }

    private fun addDefaultFileInfo() {
        addItemView(InfoHolder("Путь:", file.absolutePath), container)
        addItemView(
            InfoHolder(
                "Расширение:", file.extension
            ), container
        )
        addItemView(
            InfoHolder(
                "Изменен:", file.getLastModifiedDate()
            ), container
        )
        addItemView(
            InfoHolder(
                "Размер:", file.length().toFormattedSize()
            ), container
        )
    }

    private fun addItemView(holder: InfoHolder, container: ViewGroup?): TextView {
        @SuppressLint("InflateParams") val view =
            layoutInflater.inflate(R.layout.file_explorer_tab_info_dialog_item, null, false)
        val v = (view.findViewById<View>(R.id.item) as TextInputLayout).apply {
            editText!!.apply {
                keyListener = null
                setText(holder.info)
            }
            hint = holder.name
        }
        container!!.addView(view)
        return v.editText!!
    }

    override fun getTheme(): Int {
        return com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog
    }

    class InfoHolder(var name: String, var info: String?)
}