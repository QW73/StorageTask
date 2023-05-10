package com.qw73.fileManager.tab.file.options

import com.qw73.fileManager.R
import com.qw73.fileManager.common.dialog.OptionsDialog
import com.qw73.fileManager.extension.openFileWith
import com.qw73.fileManager.tab.file.FileExplorerTabFragment
import com.qw73.fileManager.tab.file.dialog.FileInfoDialog
import com.qw73.fileManager.tab.file.misc.FileUtils
import com.qw73.fileManager.tab.file.model.FileItem
import java.io.File

class FileOptionsHandler(private val parentFragment: FileExplorerTabFragment) {

    fun showOptions(fileItem: FileItem) {
        val selectedFiles = ArrayList<File>()
        for (item in parentFragment.selectedFiles) {
            selectedFiles.add(item.file)
        }
        if (!fileItem.isSelected) selectedFiles.add(fileItem.file)

        val title: String = fileItem.file.name
        val bottomDialog = OptionsDialog(title)
        bottomDialog.show(parentFragment.parentFragmentManager, "FileOptionsDialog")

        //______________| Options |_______________\\
        if (FileUtils.isOnlyFiles(selectedFiles)) {
            bottomDialog.addOption("Поделиться", R.drawable.ic_round_share_24, {
                FileUtils.shareFiles(selectedFiles, parentFragment.requireActivity())
                parentFragment.setSelectAll(false)
            }, true)
        }
        if (FileUtils.isSingleFile(selectedFiles)) {
            bottomDialog.addOption(
                "Открыть с помощью",
                R.drawable.ic_baseline_open_in_new_24,
                {
                    selectedFiles[0].openFileWith(true)
                    parentFragment.setSelectAll(false)
                },
                true
            )
        }
        bottomDialog.addOption("Детали", R.drawable.ic_baseline_info_24, {
            showFileInfoDialog(
                selectedFiles[0]
            )
        }, true)
    }

    private fun showFileInfoDialog(file: File) {
        FileInfoDialog(file).setUseDefaultFileInfo(true)
            .show(parentFragment.parentFragmentManager, "")
    }

}