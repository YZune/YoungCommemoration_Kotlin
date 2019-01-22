package com.suda.yzune.youngcommemoration

import android.os.Bundle
import com.suda.yzune.youngcommemoration.base_view.BaseTitleActivity
import com.suda.yzune.youngcommemoration.utils.UpdateUtils
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseTitleActivity() {
    override val layoutId: Int
        get() = R.layout.activity_about

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            tv_version.text = "版本号：${UpdateUtils.getVersionName(this)}"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
