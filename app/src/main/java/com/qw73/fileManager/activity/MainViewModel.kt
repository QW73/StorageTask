package com.qw73.fileManager.activity

import androidx.lifecycle.ViewModel
import com.qw73.fileManager.tab.BaseDataHolder

class MainViewModel : ViewModel() {

    private val dataHolders: MutableList<BaseDataHolder> = arrayListOf()

    fun addDataHolder(dataHolder: BaseDataHolder) {
        dataHolders.add(dataHolder)
    }

    fun getDataHolder(tag: String): BaseDataHolder? {
        for (dataHolder in dataHolders) {
            if (dataHolder.tag == tag) return dataHolder
        }
        return null
    }
}