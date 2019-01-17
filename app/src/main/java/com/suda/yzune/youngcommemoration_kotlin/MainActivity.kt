package com.suda.yzune.youngcommemoration_kotlin

import android.os.Bundle
import com.suda.yzune.youngcommemoration_kotlin.base_view.BaseActivity
import org.jetbrains.anko.setContentView

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityUI().setContentView(this)
    }
}
