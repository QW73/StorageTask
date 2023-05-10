package com.qw73.fileManager.tab.apps.adapter

import android.annotation.SuppressLint
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qw73.fileManager.App.Companion.showMsg
import com.qw73.fileManager.R
import com.qw73.fileManager.common.BackgroundTask
import com.qw73.fileManager.tab.apps.AppsTabFragment
import com.qw73.fileManager.tab.apps.model.Apk
import com.qw73.fileManager.tab.file.misc.FileUtils
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

open class AppListAdapter(private val list: ArrayList<Apk>, private val fragment: AppsTabFragment) :
    RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        @SuppressLint("InflateParams") val view =
            fragment.layoutInflater.inflate(R.layout.apps_tab_app_item, null)
        view.layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun showSaveDialog(file: Apk) {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setIcon(file.icon)
            .setTitle(file.name)
            .setMessage("Do you want to save this app to Download folder?")
            .setPositiveButton("Yes") { _, _ -> saveApkFile(file) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun saveApkFile(file: Apk) {
        val backgroundTask = BackgroundTask()
        val error = AtomicBoolean(false)
        backgroundTask.setTasks({
            backgroundTask.showProgressDialog(
                "Copying...",
                fragment.requireActivity()
            )
        }, {
            try {
                FileUtils.copyFile(
                    file.source,
                    file.name + ".apk",
                    File(
                        Environment.getExternalStorageDirectory(),
                        Environment.DIRECTORY_DOWNLOADS
                    ),
                    true
                )
            } catch (e: Exception) {
                error.set(true)
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post { showMsg(e.toString()) }
            }
        }) {
            if (!error.get()) showMsg("APK file has been saved in " + "/Downloads/" + file.name)
            backgroundTask.dismiss()
        }
        backgroundTask.run()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView
        var name: TextView
        var pkg: TextView
        var details: TextView

        init {
            icon = itemView.findViewById(R.id.app_icon)
            name = itemView.findViewById(R.id.app_name)
            pkg = itemView.findViewById(R.id.app_pkg)
            details = itemView.findViewById(R.id.app_details)
        }

        fun bind() {
            val position = adapterPosition
            val apk = list[position]
            name.text = apk.name
            pkg.text = apk.pkg
            details.text = apk.size
            icon.setImageDrawable(apk.icon)
            itemView.findViewById<View>(R.id.background)
                .setOnClickListener { showSaveDialog(apk) }
        }
    }
}