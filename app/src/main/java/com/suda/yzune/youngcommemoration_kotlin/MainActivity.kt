package com.suda.yzune.youngcommemoration_kotlin

import android.os.Bundle
import android.widget.FrameLayout
import com.google.android.material.appbar.AppBarLayout
import com.suda.yzune.youngcommemoration_kotlin.base_view.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resizeMarginTop()
        initEven()
    }

    private fun resizeMarginTop() {
        val statusBarMargin = getStatusBarHeight()
        (toolbar.layoutParams as FrameLayout.LayoutParams).topMargin = statusBarMargin
    }

    private fun initEven() {
        tv_add.setOnClickListener {
            startActivity<AddEventActivity>()
        }

        appbar_layout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val scrollRange = appBarLayout.totalScrollRange
                val alpha = 1 - 1.0f * Math.abs(verticalOffset) / scrollRange
                ll_info.alpha = alpha
                tv_event_main.alpha = 1 - alpha
            })
    }
}
