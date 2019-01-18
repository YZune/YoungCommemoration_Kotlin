package com.suda.yzune.youngcommemoration_kotlin

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.core.view.size
import com.google.android.material.chip.Chip
import com.suda.yzune.youngcommemoration_kotlin.base_view.BaseTitleActivity
import com.suda.yzune.youngcommemoration_kotlin.widget.SelectDayDialog
import kotlinx.android.synthetic.main.activity_add_event.*
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textColorResource

class AddEventActivity : BaseTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_add_event

    override fun onSetupSubButton(tvButton1: TextView, tvButton2: TextView) {
        val iconFont = ResourcesCompat.getFont(this, R.font.iconfont)
        tvButton1.textSize = 20f
        tvButton2.textSize = 20f
        tvButton1.typeface = iconFont
        tvButton2.typeface = iconFont

        tvButton1.text = "\uE6DB"
        tvButton2.text = "\uE6DE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        chip_m.setOnClickListener {
//            SelectDayDialog.newInstance("","").show(supportFragmentManager, "")
//        }
        cg_type.setOnCheckedChangeListener { chipGroup, id ->

        }
    }
}
