package com.suda.yzune.youngcommemoration_kotlin

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginTop
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.collapsingToolbarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.navigationView
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.drawerLayout
import org.jetbrains.anko.support.v4.viewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
import com.google.android.material.appbar.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN


class MainActivityUI : AnkoComponent<MainActivity> {

    override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {

        drawerLayout {
            val iconFont = ResourcesCompat.getFont(context, R.font.iconfont)
            val statusBarMargin = owner.getStatusBarHeight() + dip(8)
            val outValue = TypedValue()
            context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)
            id = R.id.anko_drawer_layout

            coordinatorLayout {
                appBarLayout {
                    collapsingToolbarLayout {
                        setContentScrimColor(Color.TRANSPARENT)
                        imageView {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }.lparams(matchParent, matchParent){
                            collapseMode = COLLAPSE_MODE_PARALLAX
                        }

                        view {
                            alpha = 0.6f
                            backgroundColor = Color.BLACK
                        }.lparams(matchParent, matchParent)

                        textView("\uE6A7") {
                            backgroundResource = outValue.resourceId
                            textSize = 20f
                            gravity = Gravity.CENTER
                            includeFontPadding = false
                            typeface = iconFont
                            textColor = Color.WHITE
                        }.lparams(dip(32), dip(32)){
                            collapseMode = COLLAPSE_MODE_PIN
                            topMargin = statusBarMargin
                        }

                        verticalLayout {

                            textView("在一起"){
                                gravity = Gravity.CENTER
                                textColor = Color.WHITE
                            }.lparams(matchParent, wrapContent)

                            textView("10000天"){
                                gravity = Gravity.CENTER
                                textColor = Color.WHITE
                                textSize = 48f
                                typeface = Typeface.DEFAULT_BOLD
                            }.lparams(matchParent, wrapContent)

                            textView("从2018-02-03"){
                                gravity = Gravity.CENTER
                                textColor = Color.WHITE
                            }.lparams(matchParent, wrapContent){
                                topMargin = dip(8)
                            }

                        }.lparams(matchParent, wrapContent){
                            gravity = Gravity.CENTER_VERTICAL
                        }

                        toolbar {
                            //setTitleTextColor(Color.WHITE)
                        }.lparams(matchParent, dip(80)){
                            backgroundColor = Color.TRANSPARENT
                            collapseMode = COLLAPSE_MODE_PIN
                        }

                    }.lparams(matchParent, matchParent){
                        scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                    }
                }.lparams(matchParent, dip(240))
            }.lparams(matchParent, matchParent)

            navigationView {
                id = R.id.anko_nv
                fitsSystemWindows = false
//                inflateHeaderView(R.layout.nav_header)
//                inflateMenu(R.menu.main_navigation_menu)
            }.lparams(matchParent, matchParent) {
                gravity = Gravity.START
            }
        }
    }.view
}