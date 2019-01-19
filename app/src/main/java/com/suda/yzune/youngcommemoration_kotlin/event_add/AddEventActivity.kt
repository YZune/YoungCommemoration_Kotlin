package com.suda.yzune.youngcommemoration_kotlin.event_add

import android.os.Bundle
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.suda.yzune.youngcommemoration_kotlin.R
import com.suda.yzune.youngcommemoration_kotlin.base_view.BaseTitleActivity
import kotlinx.android.synthetic.main.activity_add_event.*
import org.jetbrains.anko.find
import org.jetbrains.anko.textColorResource

class AddEventActivity : BaseTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_add_event

    private lateinit var viewModel: AddEventViewModel

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
        viewModel = ViewModelProviders.of(this).get(AddEventViewModel::class.java)
        initEvent()
    }

    private fun initEvent() {
        var chipId = 0
        cg_type.setOnCheckedChangeListener { chipGroup, id ->
            when (id) {
                R.id.chip_commemoration -> {
                    tv_event_icon.text = "\uE6C2"
                    tv_event_icon.textColorResource = R.color.pink
                    et_event.hint = "值得纪念的事情…"
                    et_date.hint = "纪念日期"
                    chipId = id
                }
                R.id.chip_birth -> {
                    tv_event_icon.text = "\uE6EB"
                    tv_event_icon.textColorResource = R.color.blue
                    et_event.hint = "寿星"
                    et_date.hint = "出生日期"
                    chipId = id
                }
                R.id.chip_lunar_birth -> {
                    tv_event_icon.text = "\uE6EB"
                    tv_event_icon.textColorResource = R.color.blue
                    et_event.hint = "寿星"
                    et_date.hint = "出生日期"
                    chipId = id
                }
                R.id.chip_rest -> {
                    tv_event_icon.text = "\uE6AC"
                    tv_event_icon.textColorResource = R.color.deepOrange
                    et_event.hint = "事件"
                    et_date.hint = "倒数日期"
                    chipId = id
                }
                else -> {
                    chipGroup.find<Chip>(chipId).isChecked = true
                }
            }
        }

        ll_fav.setOnClickListener {
            s_fav.isChecked = !s_fav.isChecked
        }

        ll_date.setOnClickListener {
            SelectDayDialog.newInstance().show(supportFragmentManager, null)
        }
    }
}
