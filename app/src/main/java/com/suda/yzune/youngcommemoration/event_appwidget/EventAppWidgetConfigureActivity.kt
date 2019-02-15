package com.suda.yzune.youngcommemoration.event_appwidget

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.suda.yzune.youngcommemoration.GlideApp
import com.suda.yzune.youngcommemoration.R
import com.suda.yzune.youngcommemoration.TipsFragment
import com.suda.yzune.youngcommemoration.base_view.BaseTitleActivity
import com.suda.yzune.youngcommemoration.main.EventListAdapter
import com.suda.yzune.youngcommemoration.utils.PreferenceUtils
import kotlinx.android.synthetic.main.event_app_widget_configure.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar

class EventAppWidgetConfigureActivity : BaseTitleActivity() {

    override val layoutId: Int
        get() = R.layout.event_app_widget_configure

    private lateinit var viewModel: AppWidgetConfigureViewModel
    private var mAppWidgetId = 0
    private var modify = false
    private var makeSure = 0

    override fun onSetupSubButton(tvButton1: TextView, tvButton2: TextView) {
        val iconFont = ResourcesCompat.getFont(this, R.font.iconfont)
        tvButton2.textSize = 20f
        tvButton2.typeface = iconFont
        tvButton2.text = "\uE6DE"
        tvButton2.setOnClickListener {
            if (!modify) {
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
            } else {
                launch {
                    val msg = withContext(Dispatchers.IO) {
                        try {
                            viewModel.updateWidget()
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
                            viewModel.widgetBean.id,
                            viewModel.selectedEvent!!,
                            viewModel.widgetBean
                        )
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
            modify = extras.getBoolean("modify", false)
            if (modify) {
                viewModel.widgetBean = extras.getParcelable("widgetData")!!
            } else {
                mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                viewModel.widgetBean.id = mAppWidgetId
            }
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
        ly_widget_1.find<LinearLayout>(R.id.ll_background).backgroundColor = viewModel.widgetBean.bgColor
        ly_widget_0.find<LinearLayout>(R.id.ll_background).backgroundColor = viewModel.widgetBean.bgColor
        initEvent()
        initList()
        if (!PreferenceUtils.getBooleanFromSP(this, "appwidget", false)) {
            TipsFragment.newInstance("为了小部件正常工作，请<b><font color='#1976D2'>允许App保持后台，加入节电白名单</font></b>。", "appwidget")
                .show(supportFragmentManager, "tips")
        }
    }

    private fun initEvent() {
        ll_with_pic.setOnClickListener {
            s_pic.isChecked = !s_pic.isChecked
        }

        s_pic.setOnCheckedChangeListener { _, isChecked ->
            viewModel.widgetBean.withPic = isChecked
            if (isChecked) {
                ly_widget_0.find<View>(R.id.iv_widget).visibility = View.VISIBLE
                ly_widget_1.find<View>(R.id.iv_widget).visibility = View.VISIBLE
            } else {
                ly_widget_0.find<View>(R.id.iv_widget).visibility = View.GONE
                ly_widget_1.find<View>(R.id.iv_widget).visibility = View.GONE
            }
        }

        sb_weight.max = 9
        sb_weight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress == 9) {
                    tv_weight.text = "自适应"
                } else {
                    tv_weight.text = "$progress : 1"
                }
                viewModel.widgetBean.weight = progress
                if (progress == 0) {
                    ly_widget_0.visibility = View.VISIBLE
                    ly_widget_1.visibility = View.GONE
                } else {
                    ly_widget_0.visibility = View.GONE
                    ly_widget_1.visibility = View.VISIBLE
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        ll_bg_color.setOnClickListener {
            buildColorPickerDialogBuilder()
                .initialColor(viewModel.widgetBean.bgColor)
                .setPositiveButton("确定") { _, colorInt, _ ->
                    viewModel.widgetBean.bgColor = colorInt
                    //ll_event_info.backgroundColor = colorInt
                    ly_widget_1.find<LinearLayout>(R.id.ll_background).backgroundColor = colorInt
                    ly_widget_0.find<LinearLayout>(R.id.ll_background).backgroundColor = colorInt
                    ly_widget_0.find<LinearLayout>(R.id.rl_background).backgroundColor = colorInt
                }
                .build()
                .show()
        }

        ll_text_color.setOnClickListener {
            buildColorPickerDialogBuilder()
                .initialColor(viewModel.widgetBean.textColor)
                .setPositiveButton("确定") { _, colorInt, _ ->
                    viewModel.widgetBean.textColor = colorInt
                    val llInfo0 = ly_widget_0.find<LinearLayout>(R.id.ll_event_info)
                    for (i in 0 until llInfo0.childCount) {
                        val v = llInfo0.getChildAt(i)
                        if (v is TextView) {
                            v.textColor = colorInt
                        }
                    }
                    val llInfo1 = ly_widget_1.find<LinearLayout>(R.id.ll_event_info)
                    for (i in 0 until llInfo1.childCount) {
                        val v = llInfo1.getChildAt(i)
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
            if (modify) {
                viewModel.selectedEvent = viewModel.showList.find {
                    it.id == viewModel.widgetBean.eventId
                }
                if (viewModel.selectedEvent == null) {
                    longToast("该事件已经被删除了哦，请重新添加一个小插件吧")
                    finish()
                    return@launch
                }
                ly_widget_1.find<LinearLayout>(R.id.ll_background).backgroundColor = viewModel.widgetBean.bgColor
                ly_widget_0.find<LinearLayout>(R.id.ll_background).backgroundColor = viewModel.widgetBean.bgColor
                ly_widget_0.find<LinearLayout>(R.id.rl_background).backgroundColor = viewModel.widgetBean.bgColor
                val llInfo0 = ly_widget_0.find<LinearLayout>(R.id.ll_event_info)
                for (i in 0 until llInfo0.childCount) {
                    val v = llInfo0.getChildAt(i)
                    if (v is TextView) {
                        v.textColor = viewModel.widgetBean.textColor
                    }
                }
                val llInfo1 = ly_widget_1.find<LinearLayout>(R.id.ll_event_info)
                for (i in 0 until llInfo1.childCount) {
                    val v = llInfo1.getChildAt(i)
                    if (v is TextView) {
                        v.textColor = viewModel.widgetBean.textColor
                    }
                }
                setPreviewContent()
                sb_weight.progress = viewModel.widgetBean.weight
                s_pic.isChecked = viewModel.widgetBean.withPic
            }
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
        val description = viewModel.selectedEvent!!.getDescriptionWithDays(this)
        ly_widget_0.find<TextView>(R.id.tv_event_widget).text = description[0]
        ly_widget_0.find<TextView>(R.id.tv_days_widget).text = description[1]
        ly_widget_1.find<TextView>(R.id.tv_event_widget).text = description[0]
        ly_widget_1.find<TextView>(R.id.tv_days_widget).text = description[1]
        if (viewModel.selectedEvent!!.msg.isBlank()) {
            ly_widget_0.find<TextView>(R.id.tv_event_msg).visibility = View.GONE
            ly_widget_1.find<TextView>(R.id.tv_event_msg).visibility = View.GONE
        } else {
            ly_widget_0.find<TextView>(R.id.tv_event_msg).visibility = View.VISIBLE
            ly_widget_0.find<TextView>(R.id.tv_event_msg).text = viewModel.selectedEvent!!.msg
            ly_widget_1.find<TextView>(R.id.tv_event_msg).visibility = View.VISIBLE
            ly_widget_1.find<TextView>(R.id.tv_event_msg).text = viewModel.selectedEvent!!.msg
        }
        GlideApp.with(this)
            .load(if (viewModel.selectedEvent!!.path.isBlank()) R.drawable.default_background else viewModel.selectedEvent!!.path)
            .override(300)
            .into(ly_widget_0.find(R.id.iv_widget))
        GlideApp.with(this)
            .load(if (viewModel.selectedEvent!!.path.isBlank()) R.drawable.default_background else viewModel.selectedEvent!!.path)
            .override(300)
            .into(ly_widget_1.find(R.id.iv_widget))
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
        if (makeSure == 0) {
            ll_with_pic.longSnackbar("点击右上角按钮保存哦~\n再点一次确认退出")
            makeSure++
            launch {
                delay(5000)
                makeSure = 0
            }
        } else {
            super.onBackPressed()
        }
    }

}

