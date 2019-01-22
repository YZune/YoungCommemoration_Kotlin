package com.suda.yzune.youngcommemoration.base_view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.suda.yzune.youngcommemoration.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout

abstract class BaseTitleActivity : BaseActivity() {

    @get:LayoutRes
    protected abstract val layoutId: Int

    open fun onSetupSubButton(tvButton1: TextView, tvButton2: TextView) {}

    private lateinit var mainTitle: TextView
    private lateinit var llContent: LinearLayout
    private lateinit var tvButton1: TextView
    private lateinit var tvButton2: TextView
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createView())
        LayoutInflater.from(this).inflate(layoutId, llContent, true)
    }

    private fun createView(): View {
        return UI {
            constraintLayout {
                backgroundColor = Color.WHITE
                scrollView = scrollView {
                    overScrollMode = View.OVER_SCROLL_NEVER
                    isVerticalScrollBarEnabled = false
                    llContent = verticalLayout {
                        topPadding = getStatusBarHeight() + dip(48)
                    }.lparams(matchParent, matchParent)

                }.lparams(matchParent, matchParent) {
                    topToTop = ConstraintSet.PARENT_ID
                    bottomToTop = ConstraintSet.PARENT_ID
                    startToStart = ConstraintSet.PARENT_ID
                    endToEnd = ConstraintSet.PARENT_ID
                }

                linearLayout {
                    id = R.id.anko_layout
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
                }.lparams(matchParent, wrapContent) {
                    topToTop = ConstraintSet.PARENT_ID
                    startToStart = ConstraintSet.PARENT_ID
                    endToEnd = ConstraintSet.PARENT_ID
                }

                view {
                    backgroundColorResource = R.color.grey
                    alpha = 0.8f
                }.lparams(matchParent, dip(1)) {
                    topToBottom = R.id.anko_layout
                    startToStart = ConstraintSet.PARENT_ID
                    endToEnd = ConstraintSet.PARENT_ID
                }
            }
        }.view
    }

    fun scrollToTop() {
        scrollView.smoothScrollTo(0, 0)
    }
}