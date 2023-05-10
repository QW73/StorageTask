package com.qw73.fileManager.tab.file

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qw73.fileManager.R
import com.qw73.fileManager.activity.MainActivity
import com.qw73.fileManager.extension.getFormattedFileCount
import com.qw73.fileManager.tab.BaseDataHolder
import com.qw73.fileManager.tab.BaseTabFragment
import com.qw73.fileManager.tab.file.adapter.FileListAdapter
import com.qw73.fileManager.tab.file.adapter.PathHistoryAdapter
import com.qw73.fileManager.tab.file.misc.FileOpener
import com.qw73.fileManager.tab.file.misc.FileUtils
import com.qw73.fileManager.tab.file.model.FileItem
import com.qw73.fileManager.tab.file.options.FileOptionsHandler
import com.qw73.fileManager.util.PrefsUtils
import java.io.File
import java.util.*

class FileExplorerTabFragment : BaseTabFragment() {

    val files = ArrayList<FileItem>()
    var pathHistory = ArrayList<File>()

    private lateinit var fileList: RecyclerView
    private lateinit var pathHistoryRv: RecyclerView
    private lateinit var placeHolder: View
    private lateinit var fileOptionsHandler: FileOptionsHandler
    private var requireRefresh = false
    var previousDirectory: File? = null

    @JvmField
    var currentDirectory: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.file_explorer_tab_fragment, container, false)
        fileList = view.findViewById(R.id.rv)
        pathHistoryRv = view.findViewById(R.id.path_history)
        placeHolder = view.findViewById(R.id.place_holder)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareBottomBarView()
        initFileList()
        loadData()
        restoreRecyclerViewState()
    }

    private fun loadData() {
        setCurrentDirectory((dataHolder as FileExplorerTabDataHolder).activeDirectory!!)
    }

    private fun prepareBottomBarView() {

        bottomBarView!!.clear()

        bottomBarView!!.addItem(
            "Обновить",
            R.drawable.ic_baseline_restart_alt_24
        ) { refresh() }
        bottomBarView!!.addItem(
            "Фильтр",
            R.drawable.ic_baseline_sort_24
        ) { view: View -> showSortOptionsMenu(view) }
    }

    private fun showSortOptionsMenu(view: View) {
        val popupMenu = PopupMenu(requireActivity(), view)
        popupMenu.menu.add("Сортировка по:").isEnabled = false
        popupMenu.menu.add("Имени (A-Z)").setCheckable(true).isChecked =
            PrefsUtils.FileExplorerTab.sortingMethod == PrefsUtils.SORT_NAME_A2Z
        popupMenu.menu.add("Имени (Z-A)").setCheckable(true).isChecked =
            PrefsUtils.FileExplorerTab.sortingMethod == PrefsUtils.SORT_NAME_Z2A
        popupMenu.menu.add("Убыванию размера").setCheckable(true).isChecked =
            PrefsUtils.FileExplorerTab.sortingMethod == PrefsUtils.SORT_SIZE_BIGGER
        popupMenu.menu.add("Возрастанию размера").setCheckable(true).isChecked =
            PrefsUtils.FileExplorerTab.sortingMethod == PrefsUtils.SORT_SIZE_SMALLER
        popupMenu.menu.add("Дате изменения").setCheckable(true).isChecked =
            PrefsUtils.FileExplorerTab.sortingMethod == PrefsUtils.SORT_DATE_NEWER
        popupMenu.menu.add("Расширению").setCheckable(true).isChecked =
            PrefsUtils.FileExplorerTab.sortingMethod == PrefsUtils.SORT_DATE_EXTENSION
        popupMenu.menu.add("Другие опции").isEnabled = false
        popupMenu.menu.add("Сначала папки").setCheckable(true).isChecked =
            PrefsUtils.FileExplorerTab.listFoldersFirst()
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            menuItem.isChecked = !menuItem.isChecked
            when (menuItem.title.toString()) {
                "Имени (A-Z)" -> {
                    PrefsUtils.FileExplorerTab.sortingMethod = PrefsUtils.SORT_NAME_A2Z
                }
                "Имени (Z-A)" -> {
                    PrefsUtils.FileExplorerTab.sortingMethod = PrefsUtils.SORT_NAME_Z2A
                }
                "Убыванию размера" -> {
                    PrefsUtils.FileExplorerTab.sortingMethod = PrefsUtils.SORT_SIZE_BIGGER
                }
                "Возрастанию размера" -> {
                    PrefsUtils.FileExplorerTab.sortingMethod = PrefsUtils.SORT_SIZE_SMALLER
                }
                "Расширению" -> {
                    PrefsUtils.FileExplorerTab.sortingMethod = PrefsUtils.SORT_DATE_EXTENSION
                }
                "Дате изменения" -> {
                    PrefsUtils.FileExplorerTab.sortingMethod = PrefsUtils.SORT_DATE_NEWER
                }
                "Сначала папки" -> {
                    PrefsUtils.FileExplorerTab.setListFoldersFirst(menuItem.isChecked)
                }
            }
            refresh()
            true
        }
        popupMenu.show()
    }

    override fun onBackPressed(): Boolean {
        if (selectedFiles.size > 0) {
            setSelectAll(false)
            return true
        }
        val parent = currentDirectory?.parentFile
        if (parent != null && parent.exists() && parent.canRead()) {
            setCurrentDirectory(currentDirectory?.parentFile!!)
            restoreRecyclerViewState()
            (dataHolder as FileExplorerTabDataHolder).selectedFiles.clear()
            return true
        }
        if (tag != null && !tag!!.startsWith("0_")) {
            return true
        }
        return false
    }

    override fun createNewDataHolder(): BaseDataHolder {
        val dataHolder = FileExplorerTabDataHolder(tag!!)
        dataHolder.activeDirectory =
            if (currentDirectory == null) defaultHomeDirectory else currentDirectory
        return dataHolder
    }

    override fun onStop() {
        super.onStop()
        requireRefresh = true
    }

    override fun onPause() {
        super.onPause()
        requireRefresh = true
    }

    override fun onResume() {
        super.onResume()
        if (requireRefresh) {
            requireRefresh = false
            refresh()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectAll(select: Boolean) {
        if (!select) (dataHolder as FileExplorerTabDataHolder).selectedFiles.clear()
        for (item in files) {
            item.isSelected = select
            if (select) {
                (dataHolder as FileExplorerTabDataHolder).selectedFiles.add(item.file)
            }
        }
        fileList.adapter?.notifyDataSetChanged()
    }

    val selectedFiles: ArrayList<FileItem>
        get() {
            val list = ArrayList<FileItem>()
            for (item in files) {
                if (item.isSelected) list.add(item)
            }
            return list
        }
    fun showPlaceholder(isShow: Boolean) {
        placeHolder.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    private fun initFileList() {
        fileList.adapter = FileListAdapter(this)
        fileList.setHasFixedSize(false)
        initPathHistory()
    }

    private fun initPathHistory() {
        pathHistoryRv.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        pathHistoryRv.adapter = PathHistoryAdapter(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        val fileExplorerTabDataHolder = mainViewModel!!.getDataHolder(
            tag!!
        ) as FileExplorerTabDataHolder?
        fileExplorerTabDataHolder?.recyclerViewStates?.put(
            currentDirectory!!, fileList.layoutManager!!
                .onSaveInstanceState()!!
        )
    }

    fun setCurrentDirectory(file: File) {
        if (currentDirectory != null) previousDirectory = currentDirectory
        currentDirectory = file
        if (previousDirectory != null) (dataHolder as FileExplorerTabDataHolder).recyclerViewStates[previousDirectory!!] =
            fileList.layoutManager!!
                .onSaveInstanceState()!!
        prepareFiles()
        updatePathHistoryList()
        refreshFileList()
        (dataHolder as FileExplorerTabDataHolder).activeDirectory = currentDirectory
    }

    fun restoreRecyclerViewState() {
        val savedState =
            (dataHolder as FileExplorerTabDataHolder).recyclerViewStates[currentDirectory]
        if (savedState != null) {
            fileList.layoutManager?.onRestoreInstanceState(savedState)
            (dataHolder as FileExplorerTabDataHolder).recyclerViewStates.remove(currentDirectory)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshFileList() {
        fileList.adapter?.notifyDataSetChanged()
        pathHistoryRv.adapter?.notifyDataSetChanged()
        pathHistoryRv.scrollToPosition(pathHistoryRv.adapter!!.itemCount - 1)
        fileList.scrollToPosition(0)
        if (toolbar != null) toolbar!!.subtitle =
            currentDirectory?.getFormattedFileCount()
    }

    private fun refresh() {
        setCurrentDirectory(currentDirectory!!)
        restoreRecyclerViewState()
    }

    private val defaultHomeDirectory: File
        get() = Environment.getExternalStorageDirectory()

    private fun prepareFiles() {
        if (currentDirectory == null) {
            loadData()
            return
        }
        files.clear()
        val files = currentDirectory!!.listFiles()
        if (files != null) {
            for (comparator in FileUtils.comparators) {
                Arrays.sort(files, comparator)
            }
            for (file in files) {
                val fileItem = FileItem(file)
                if ((dataHolder as FileExplorerTabDataHolder).selectedFiles.contains(fileItem.file)) {
                    fileItem.isSelected = true
                }
                this.files.add(fileItem)
            }
        }
    }

    fun showFileOptions(fileItem: FileItem?) {
        if (!this::fileOptionsHandler.isInitialized) {
            fileOptionsHandler = FileOptionsHandler(this)
        }
        fileOptionsHandler.showOptions(fileItem!!)
    }

    fun openFile(fileItem: FileItem) {
        FileOpener(requireActivity() as MainActivity).openFile(fileItem.file)
    }

    private fun updatePathHistoryList() {
        val list = ArrayList<File>()
        var file = currentDirectory
        while (file != null && file.canRead()) {
            list.add(file)
            file = file.parentFile?.takeIf { it.canRead() }
        }
        list.reverse()
        if (file != null && !file.name.contains("emulated")) {
            list.add(file)
        }
        pathHistory = list
    }
}