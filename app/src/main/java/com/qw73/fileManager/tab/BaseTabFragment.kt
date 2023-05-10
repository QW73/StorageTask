package com.qw73.fileManager.tab

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.qw73.fileManager.activity.MainActivity
import com.qw73.fileManager.activity.MainViewModel
import com.qw73.fileManager.common.view.BottomBarView

abstract class BaseTabFragment : Fragment() {
    var mainViewModel: MainViewModel? = null
        get() {
            if (field == null) {
                field = ViewModelProvider(requireActivity())[MainViewModel::class.java]
            }
            return field
        }
        private set

    var bottomBarView: BottomBarView? = null
        get() {
            if (field == null) field = (requireActivity() as MainActivity).bottomBarView
            return field
        }
        private set

    var toolbar: MaterialToolbar? = null
        get() {
            if (field == null) field = (requireActivity() as MainActivity).toolbar
            return field
        }
        private set

    var dataHolder: BaseDataHolder? = null
        get() {
            if (field == null && mainViewModel!!.getDataHolder(tag!!).also { field = it } == null) {
                field = createNewDataHolder()
                mainViewModel!!.addDataHolder(field!!)
            }
            return field
        }
        private set

    abstract fun onBackPressed(): Boolean
    abstract fun createNewDataHolder(): BaseDataHolder

    companion object {
        const val DEFAULT_TAB_FRAGMENT_PREFIX = "0_FileExplorerTabFragment_"
    }
}