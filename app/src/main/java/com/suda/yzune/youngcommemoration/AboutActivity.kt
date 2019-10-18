package com.suda.yzune.youngcommemoration

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import android.widget.TextView
import com.bumptech.glide.Glide
import com.suda.yzune.youngcommemoration.base_view.BaseTitleActivity
import com.suda.yzune.youngcommemoration.utils.UpdateUtils
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.textColorResource

class AboutActivity : BaseTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_about

    override fun onSetupSubButton(tvButton1: TextView, tvButton2: TextView) {
        val iconFont = ResourcesCompat.getFont(this, R.font.iconfont)
        tvButton2.textSize = 20f
        tvButton2.typeface = iconFont
        tvButton2.text = "\uE6C2"
        tvButton2.textColorResource = R.color.pink
        tvButton2.setOnClickListener {
            it.longSnackbar("去应用商店给个五星好评呗\no(*////▽////*)q")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            tv_version.text = "版本号：${UpdateUtils.getVersionName(this)}"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Glide.with(this)
            .load("https://ws3.sinaimg.cn/large/006tNc79gy1fznkoyx8amj30u00u0wgl.jpg")
            .into(iv_share)
    }
}
