package com.qw73.fileManager.tab.file.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qw73.fileManager.R
import com.qw73.fileManager.extension.isExternalStorageFolder
import com.qw73.fileManager.tab.file.FileExplorerTabFragment
import com.qw73.fileManager.tab.file.dialog.FileInfoDialog
import com.qw73.fileManager.tab.file.misc.FileUtils
import com.qw73.fileManager.util.Utils

class PathHistoryAdapter(private val parentFragment: FileExplorerTabFragment) :
    RecyclerView.Adapter<PathHistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(parent.context).inflate(
                R.layout.file_explorer_tab_path_history_view,
                null
            )
        v.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return parentFragment.pathHistory.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var label: TextView
        fun bind() {
            val position = adapterPosition
            val file = parentFragment.pathHistory[position]
            label.text =
                if (file.isExternalStorageFolder()) FileUtils.INTERNAL_STORAGE else file.name
            label.setTextColor(
                if (position == itemCount - 1) Utils.getColorAttribute(
                    com.google.android.material.R.attr.colorPrimary,
                    parentFragment.requireActivity()
                ) else Utils.getColorAttribute(
                    com.google.android.material.R.attr.colorOutline,
                    parentFragment.requireActivity()
                )
            )
            itemView.setOnClickListener {
                parentFragment.setCurrentDirectory(file)
                parentFragment.restoreRecyclerViewState()
            }
            itemView.setOnLongClickListener {
                FileInfoDialog(file).setUseDefaultFileInfo(true).show(
                    parentFragment.parentFragmentManager, ""
                )
                true
            }
        }

        init {
            label = itemView.findViewById(R.id.text)
        }
    }
}