package com.suda.yzune.youngcommemoration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import com.suda.yzune.youngcommemoration.bean.UpdateInfoBean
import com.suda.yzune.youngcommemoration.utils.UpdateUtils
import kotlinx.android.synthetic.main.fragment_update.*

class UpdateFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_update

    private lateinit var updateInfo: UpdateInfoBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            updateInfo = it.getParcelable("updateInfo")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_old_version.text = "当前版本：" + UpdateUtils.getVersionName(context!!.applicationContext)
        tv_new_version.text = "最新版本：" + updateInfo.VersionName
        tv_info.text = updateInfo.VersionInfo
        tv_visit.setOnClickListener {
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val contentUrl = Uri.parse("https://www.coolapk.com/apk/com.suda.yzune.youngcommemoration")
            intent.data = contentUrl
            context!!.startActivity(intent)
            dismiss()
        }
        ib_close.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: UpdateInfoBean) =
            UpdateFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("updateInfo", arg)
                }
            }
    }
}
