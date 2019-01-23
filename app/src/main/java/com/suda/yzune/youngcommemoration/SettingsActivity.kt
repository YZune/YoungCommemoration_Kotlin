package com.suda.yzune.youngcommemoration

import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.suda.yzune.youngcommemoration.base_view.BaseTitleActivity
import com.suda.yzune.youngcommemoration.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.design.longSnackbar

class SettingsActivity : BaseTitleActivity() {
    override val layoutId: Int
        get() = R.layout.activity_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ll_nav_bar_color.setOnClickListener {
            ColorPickerDialogBuilder
                .with(this)
                .setTitle("选取颜色")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setNegativeButton("取消") { _, _ -> }
                .initialColor(
                    PreferenceUtils.getIntFromSP(
                        applicationContext,
                        "nav_bar_color",
                        ContextCompat.getColor(applicationContext, R.color.colorAccent)
                    )
                )
                .setPositiveButton("确定") { _, colorInt, _ ->
                    PreferenceUtils.saveIntToSP(applicationContext, "nav_bar_color", colorInt)
                    it.longSnackbar("重启App后生效哦~")
                }
                .build()
                .show()
        }
    }
}