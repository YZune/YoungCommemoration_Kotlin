package com.suda.yzune.youngcommemoration.event_appwidget

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.suda.yzune.youngcommemoration.GlideApp
import com.suda.yzune.youngcommemoration.R
import com.suda.yzune.youngcommemoration.base_view.BaseTitleActivity
import com.suda.yzune.youngcommemoration.main.EventListAdapter
import kotlinx.android.synthetic.main.event_app_widget.*
import kotlinx.android.synthetic.main.event_app_widget_configure.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar

class EventAppWidgetConfigureActivity : BaseTitleActivity() {

    override val layoutId: Int
        get() = R.layout.event_app_widget_configure

    private lateinit var viewModel: AppWidgetConfigureViewModel
    private var mAppWidgetId = 0

    override fun onSetupSubButton(tvButton1: TextView, tvButton2: TextView) {
        val iconFont = ResourcesCompat.getFont(this, R.font.iconfont)
        tvButton2.textSize = 20f
        tvButton2.typeface = iconFont
        tvButton2.text = "\uE6DE"
        tvButton2.setOnClickListener {
            if (viewModel.selectedEvent == null) {
                ll_with_pic.longSnackbar("你还没有选择事件哦，往下划动看看。")
            } else {
                launch {
                    val msg = withContext(Dispatchers.IO) {
                        try {
                            viewModel.insertWidget()
                            "ok"
                        } catch (e: Exception) {
                            "发生异常>_<" + e.message
                        }
                    }
                    if (msg == "ok") {
                        toast("保存成功☆´∀｀☆")
                        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
                        EventAppWidget.updateAppWidget(
                            applicationContext,
                            appWidgetManager,
                            mAppWidgetId,
                            viewModel.selectedEvent!!,
                            viewModel.widgetBean
                        )
                        val resultValue = Intent()
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                        setResult(Activity.RESULT_OK, resultValue)
                        finish()
                    } else {
                        longToast(msg)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AppWidgetConfigureViewModel::class.java)
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            viewModel.widgetBean.id = mAppWidgetId
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            showWallpaper()
        }
        initEvent()
        initList()
    }

    private fun initEvent() {
        ll_with_pic.setOnClickListener {
            s_pic.isChecked = !s_pic.isChecked
        }

        s_pic.setOnCheckedChangeListener { _, isChecked ->
            viewModel.widgetBean.withPic = isChecked
            if (isChecked) {
                iv_widget.visibility = View.VISIBLE
            } else {
                iv_widget.visibility = View.GONE
            }
        }

        ll_with_shadow.setOnClickListener {
            s_shadow.isChecked = !s_shadow.isChecked
        }

        s_shadow.setOnCheckedChangeListener { _, isChecked ->
            viewModel.widgetBean.withShadow = isChecked
            if (isChecked) {
                ll_background.translationZ = dip(4).toFloat()
            } else {
                ll_background.translationZ = 0f
            }
        }

        ll_bg_color.setOnClickListener {
            buildColorPickerDialogBuilder()
                .initialColor(viewModel.widgetBean.bgColor)
                .setPositiveButton("确定") { _, colorInt, _ ->
                    viewModel.widgetBean.bgColor = colorInt
                    ll_background.backgroundTintList = ColorStateList.valueOf(colorInt)
                }
                .build()
                .show()
        }

        ll_text_color.setOnClickListener {
            buildColorPickerDialogBuilder()
                .initialColor(viewModel.widgetBean.textColor)
                .setPositiveButton("确定") { _, colorInt, _ ->
                    viewModel.widgetBean.textColor = colorInt
                    for (i in 0 until ll_event_info.childCount) {
                        val v = ll_event_info.getChildAt(i)
                        if (v is TextView) {
                            v.textColor = colorInt
                        }
                    }
                }
                .build()
                .show()
        }
    }

    private fun initList() {
        launch {
            viewModel.showList.addAll(withContext(Dispatchers.IO) {
                viewModel.getAllEvents()
            })
            val adapter = EventListAdapter(R.layout.item_event, viewModel.showList)
            adapter.setOnItemClickListener { _, _, position ->
                viewModel.selectedEvent = viewModel.showList[position]
                viewModel.widgetBean.eventId = viewModel.selectedEvent!!.id
                setPreviewContent()
                scrollToTop()
            }
            adapter.emptyView = UI {
                frameLayout {

                    verticalLayout {

                        imageView(R.drawable.ic_launcher_foreground) {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }.lparams(dip(240), dip(240))

                        textView("这里空空的呢\n要不试试加点东西？") {
                            gravity = Gravity.CENTER
                        }.lparams(matchParent, wrapContent) {
                            topMargin = -dip(64)
                        }

                    }.lparams(wrapContent, wrapContent) {
                        gravity = Gravity.CENTER
                    }
                }
            }.view
            rv_events.layoutManager = LinearLayoutManager(this@EventAppWidgetConfigureActivity)
            rv_events.adapter = adapter
        }
    }

    private fun setPreviewContent() {
        val description = viewModel.selectedEvent!!.getDescriptionWithDays()
        tv_event_widget.text = description[0]
        tv_days_widget.text = description[1]
        if (viewModel.selectedEvent!!.msg.isBlank()) {
            tv_event_msg.visibility = View.GONE
        } else {
            tv_event_msg.visibility = View.VISIBLE
            tv_event_msg.text = viewModel.selectedEvent!!.msg
        }
        GlideApp.with(this)
            .load(if (viewModel.selectedEvent!!.path.isBlank()) R.drawable.default_background else viewModel.selectedEvent!!.path)
            .override(300)
            .into(iv_widget)
    }

    private fun buildColorPickerDialogBuilder(): ColorPickerDialogBuilder {
        return ColorPickerDialogBuilder
            .with(this)
            .setTitle("选取颜色")
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setNegativeButton("取消") { _, _ -> }
    }

    private fun showWallpaper() {
        val wallpaperManager = WallpaperManager.getInstance(this)
        val wallpaperDrawable = wallpaperManager.drawable
        GlideApp.with(this)
            .load(wallpaperDrawable)
            .override(300)
            .into(iv_wallpaper)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showWallpaper()
                } else {
                    longToast("你取消了授权，无法使用桌面壁纸预览")
                }
            }
        }
    }

    override fun onBackPressed() {
        ll_with_pic.longSnackbar("点击右上角按钮保存哦~")
    }

}

