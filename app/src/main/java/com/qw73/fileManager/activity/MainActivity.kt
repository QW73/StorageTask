package com.qw73.fileManager.activity

import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.appbar.MaterialToolbar
import com.qw73.fileManager.App.Companion.showMsg
import com.qw73.fileManager.R
import com.qw73.fileManager.common.view.BottomBarView
import com.qw73.fileManager.tab.BaseTabFragment
import com.qw73.fileManager.tab.file.FileExplorerTabFragment
import com.qw73.fileManager.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private var confirmExit = false
    lateinit var toolbar: MaterialToolbar
    lateinit var bottomBarView: BottomBarView

    private lateinit var fragmentContainerView: FragmentContainerView

    private val activeFragment: BaseTabFragment
        get() = supportFragmentManager.findFragmentById(R.id.fragment_container) as BaseTabFragment

    override fun init() {
        loadDefaultTab()
    }

    private fun loadDefaultTab() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                FileExplorerTabFragment(),
                BaseTabFragment.DEFAULT_TAB_FRAGMENT_PREFIX + generateRandomTag()
            )
            .setReorderingAllowed(true)
            .commit()
    }

    fun generateRandomTag(): String {
        return Utils.getRandomString(16)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentContainerView = findViewById(R.id.fragment_container)
        toolbar = findViewById(R.id.toolbar)
        bottomBarView = findViewById(R.id.bottom_bar_view)

        setSupportActionBar(toolbar)
        checkPermissions()
    }

    override fun onBackPressed() {
        if (activeFragment.onBackPressed()) {
            return
        }
        if (!confirmExit) {
            confirmExit = true
            showMsg("Нажмите еще раз для выхода из приложения")
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                confirmExit = false
            }
            return
        }
        super.onBackPressed()
    }

}