package com.suda.yzune.youngcommemoration_kotlin.base_view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.res.ResourcesCompat
import com.suda.yzune.youngcommemoration_kotlin.R
import org.jetbrains.anko.*

abstract class BaseTitleActivity : BaseActivity() {

    @get:LayoutRes
    protected abstract val layoutId: Int

    open fun onSetupSubButton(tvButton1: TextView, tvButton2: TextView){}

    private lateinit var mainTitle: TextView
    private lateinit var tvButton1: TextView
    private lateinit var tvButton2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        find<LinearLayout>(R.id.ll_root).addView(createTitleBar(), 0)
    }

    private fun createTitleBar(): View {
        return UI {
            verticalLayout {

                linearLayout {
                    topPadding = getStatusBarHeight()
                    backgroundColor = Color.WHITE
                    val outValue = TypedValue()
                    context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

                    imageButton(R.drawable.ic_back) {
                        backgroundResource = outValue.resourceId
                        padding = dip(8)
                        setOnClickListener {
                            onBackPressed()
                        }
                    }.lparams(wrapContent, dip(48))

                    mainTitle = textView(this@BaseTitleActivity.title) {
                        gravity = Gravity.CENTER_VERTICAL
                        textSize = 16f
                        typeface = Typeface.DEFAULT_BOLD
                    }.lparams(wrapContent, dip(48)) {
                        weight = 1f
                    }

                    tvButton1 = textView {
                        gravity = Gravity.CENTER_VERTICAL
                        backgroundResource = outValue.resourceId
                    }.lparams(wrapContent, dip(48))

                    tvButton2 = textView {
                        gravity = Gravity.CENTER_VERTICAL
                        backgroundResource = outValue.resourceId
                        horizontalPadding = dip(24)
                    }.lparams(wrapContent, dip(48))

                    onSetupSubButton(tvButton1, tvButton2)
                }

                view {
                    backgroundColorResource = R.color.grey
                    alpha = 0.5f
                }.lparams(wrapContent, dip(1))
            }
        }.view
    }
}