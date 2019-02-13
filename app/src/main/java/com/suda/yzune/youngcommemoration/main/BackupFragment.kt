package com.suda.yzune.youngcommemoration.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.BaseDialogFragment
import android.view.View
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.suda.yzune.youngcommemoration.R
import gdut.bsx.share2.FileUtil
import gdut.bsx.share2.Share2
import gdut.bsx.share2.ShareContentType
import kotlinx.android.synthetic.main.fragment_backup.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.util.regex.Pattern

class BackupFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_backup

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ib_close.setOnClickListener {
            dismiss()
        }

        tv_export.setOnClickListener {
            tv_export.text = "导出中…请稍候"
            launch {
                val task = withContext(Dispatchers.IO) {
                    try {
                        viewModel.exportData(Environment.getExternalStorageDirectory().absolutePath)
                        "ok"
                    } catch (e: Exception) {
                        e.message
                    }
                }
                if (task == "ok") {
                    toast("导出成功")
                    dismiss()
                } else {
                    longToast("出现异常>_<\n$task")
                }
            }
        }

        tv_share.setOnClickListener {
            tv_share.text = "导出中…请稍候"
            launch {
                val task = withContext(Dispatchers.IO) {
                    try {
                        viewModel.exportData(Environment.getExternalStorageDirectory().absolutePath)
                    } catch (e: Exception) {
                        null
                    }
                }
                if (task != null) {
                    Share2.Builder(activity)
                        .setContentType(ShareContentType.FILE)
                        .setShareFileUri(FileUtil.getFileUri(activity, null, File(task)))
                        .setTitle("导出并分享")
                        .build()
                        .shareBySystem()
                    dismiss()
                } else {
                    longToast("出现异常>_<\n$task")
                }
            }
        }

        tv_import.setOnClickListener {
            ll_export.visibility = View.GONE
            ll_import.visibility = View.VISIBLE
            tv_title.text = "导入"
            tv_tip.text = "采用增量导入\n要从哪里导入呢"
        }

        val basePath = Environment.getExternalStorageDirectory().absolutePath

        val defaultPath = if (basePath.endsWith(File.separator)) {
            "${basePath}咩咩/"
        } else {
            "$basePath/咩咩/"
        }

        val qqPath = if (basePath.endsWith(File.separator)) {
            "${basePath}tencent/QQfile_recv/"
        } else {
            "$basePath/tencent/QQfile_recv/"
        }

        val timPath = if (basePath.endsWith(File.separator)) {
            "${basePath}tencent/TIMfile_recv/"
        } else {
            "$basePath/tencent/TIMfile_recv/"
        }

        val wechatPath = if (basePath.endsWith(File.separator)) {
            "${basePath}tencent/micromsg/Download/"
        } else {
            "$basePath/tencent/micromsg/Download/"
        }

        tv_default.setOnClickListener {
            selectFile(defaultPath)
        }

        tv_qq.setOnClickListener {
            selectFile(qqPath)
        }

        tv_tim.setOnClickListener {
            selectFile(timPath)
        }

        tv_wechat.setOnClickListener {
            selectFile(wechatPath)
        }

        tv_self.setOnClickListener {
            selectFile(basePath)
        }
    }

    private fun selectFile(path: String) {
        MaterialFilePicker()
            .withActivity(activity)
            .withRequestCode(1)
            .withPath(path)
            .withFilter(Pattern.compile(".*\\.young$")) // Filtering files and directories by file name using regexp
            .start()
    }

    companion object {
        @JvmStatic
        fun newInstance() = BackupFragment()
    }

}
