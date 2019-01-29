package com.suda.yzune.youngcommemoration.event_add

import android.Manifest
import android.appwidget.AppWidgetManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.github.florent37.glidepalette.GlidePalette
import com.suda.yzune.youngcommemoration.GlideApp
import com.suda.yzune.youngcommemoration.GlideOptions.bitmapTransform
import com.suda.yzune.youngcommemoration.R
import com.suda.yzune.youngcommemoration.base_view.BaseTitleActivity
import com.suda.yzune.youngcommemoration.bean.EventBean
import com.suda.yzune.youngcommemoration.event_appwidget.EventAppWidget
import com.suda.yzune.youngcommemoration.utils.GlideAppEngine
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.item_event.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar

class AddEventActivity : BaseTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_add_event

    private lateinit var viewModel: AddEventViewModel
    private val REQUEST_CODE_CHOOSE_BG = 23
    private var makeSure = 0

    override fun onSetupSubButton(tvButton1: TextView, tvButton2: TextView) {
        val iconFont = ResourcesCompat.getFont(this, R.font.iconfont)
        tvButton1.textSize = 20f
        tvButton2.textSize = 20f
        tvButton1.typeface = iconFont
        tvButton2.typeface = iconFont

        tvButton1.text = "\uE6DB"
        tvButton2.text = "\uE6DE"

        tvButton1.setOnClickListener {
            pickImage()
        }

        tvButton2.setOnClickListener {
            if (viewModel.event.content.isBlank()) {
                tvButton2.longSnackbar("你忘记写点东西了哦<(￣︶￣)>")
            } else {
                launch {
                    val msg = withContext(Dispatchers.IO) {
                        try {
                            viewModel.save()
                            if (!viewModel.isNew) {
                                val wList = viewModel.getEventWidgets()
                                withContext(Dispatchers.Main) {
                                    if (wList.isNotEmpty()) {
                                        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
                                        for (w in wList) {
                                            EventAppWidget.updateAppWidget(
                                                applicationContext,
                                                appWidgetManager,
                                                w.id,
                                                viewModel.event,
                                                w
                                            )
                                        }
                                    }
                                }
                            }
                            "ok"
                        } catch (e: Exception) {
                            "发生异常>_<" + e.message
                        }
                    }
                    if (msg == "ok") {
                        toast("保存成功☆´∀｀☆")
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
        viewModel = ViewModelProviders.of(this).get(AddEventViewModel::class.java)
        initEvent()

        if (intent.extras?.getParcelable<EventBean>("event") == null) {
            viewModel.event = EventBean()
            viewModel.isNew = true
        } else {
            viewModel.event = intent.extras!!.getParcelable("event")!!
            viewModel.isNew = false
            btn_delete.visibility = View.VISIBLE
        }
        initView()
    }

    private fun initEvent() {
        cv_event.setOnClickListener {
            pickImage()
        }

        btn_delete.setOnLongClickListener {
            launch {
                val msg = withContext(Dispatchers.IO) {
                    try {
                        viewModel.delete()
                        "ok"
                    } catch (e: Exception) {
                        "发生异常>_<" + e.message
                    }
                }
                if (msg == "ok") {
                    longToast("删除成功")
                    finish()
                } else {
                    longToast(msg)
                }
            }
            return@setOnLongClickListener true
        }

        var chipId = 0
        cg_type.setOnCheckedChangeListener { chipGroup, id ->
            when (id) {
                R.id.chip_commemoration -> {
                    tv_event_icon.text = "\uE6C2"
                    tv_event_icon.textColorResource = R.color.pink
                    et_event.hint = "值得纪念的事情…"
                    et_date.hint = "纪念日期"
                    chipId = id
                    viewModel.event.type = 0
                }
                R.id.chip_birth -> {
                    tv_event_icon.text = "\uE6EB"
                    tv_event_icon.textColorResource = R.color.blue
                    et_event.hint = "寿星"
                    et_date.hint = "出生日期"
                    chipId = id
                    viewModel.event.type = 1
                }
                R.id.chip_lunar_birth -> {
                    tv_event_icon.text = "\uE6EB"
                    tv_event_icon.textColorResource = R.color.blue
                    et_event.hint = "寿星"
                    et_date.hint = "出生日期"
                    chipId = id
                    viewModel.event.type = 2
                }
                R.id.chip_rest -> {
                    tv_event_icon.text = "\uE6AC"
                    tv_event_icon.textColorResource = R.color.deepOrange
                    et_event.hint = "事件"
                    et_date.hint = "倒数日期"
                    chipId = id
                    viewModel.event.type = 3
                }
                else -> {
                    chipGroup.find<Chip>(chipId).isChecked = true
                }
            }
            refreshPreview()
        }

        ll_fav.setOnClickListener {
            s_fav.isChecked = !s_fav.isChecked
        }

        s_fav.setOnCheckedChangeListener { _, isChecked ->
            viewModel.event.isFav = isChecked
        }

        ll_date.setOnClickListener {
            SelectDayDialog.newInstance().show(supportFragmentManager, null)
        }

        et_event.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.event.content = s.toString()
                refreshPreview()
            }
        })

        et_msg.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.event.msg = s.toString()
                refreshPreview()
            }
        })
    }

    fun initView() {
        (cg_type.getChildAt(viewModel.event.type) as Chip).isChecked = true
        refreshPreview()
        et_event.setText(viewModel.event.content)
        et_date.text = "${viewModel.event.year} - ${viewModel.event.month + 1} - ${viewModel.event.day}"
        et_msg.setText(viewModel.event.msg)
        s_fav.isChecked = viewModel.event.isFav
        showImage()
    }

    private fun refreshPreview() {
        val description = viewModel.event.getDescriptionWithDays(this)
        tv_content.text = description[0]
        tv_count.text = description[1]
        tv_date.text = description[2]

        if (viewModel.event.msg.isBlank()) {
            tv_msg.visibility = View.GONE
        } else {
            tv_msg.visibility = View.VISIBLE
            tv_msg.text = viewModel.event.msg
        }
    }

    private fun pickImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            Matisse.from(this)
                .choose(setOf(MimeType.GIF, MimeType.PNG, MimeType.JPEG))
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(dip(120))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(GlideAppEngine())
                .forResult(REQUEST_CODE_CHOOSE_BG)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Matisse.from(this)
                        .choose(setOf(MimeType.GIF, MimeType.PNG, MimeType.JPEG))
                        .countable(true)
                        .maxSelectable(1)
                        .gridExpectedSize(dip(120))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(GlideAppEngine())
                        .forResult(REQUEST_CODE_CHOOSE_BG)
                } else {
                    longToast("你取消了授权，无法选择图片")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE_BG && resultCode == RESULT_OK) {
            viewModel.event.path = Matisse.obtainResult(data)[0].toString()
            showImage()
        }
    }

    private fun showImage() {
        if (viewModel.event.path.isBlank()) {
            GlideApp.with(this)
                .load(R.drawable.default_background)
                .override(300)
                .apply(bitmapTransform(BlurTransformation(25)))
                .into(iv_pic_bg)

        } else {
            GlideApp.with(this)
                .load(viewModel.event.path)
                .override(300)
                .into(iv_pic)
            GlideApp.with(this)
                .load(viewModel.event.path)
                .override(300)
                .apply(bitmapTransform(BlurTransformation(25))).listener(
                    GlidePalette
                        .with(viewModel.event.path)
                        .intoCallBack {
                            val color = it!!.getLightVibrantColor(ContextCompat.getColor(this, R.color.blue_50))
                            v_bg.backgroundColor = color
                        }

                )
                .into(iv_pic_bg)
        }
    }

    override fun onBackPressed() {
        if (makeSure == 0) {
            ll_date.longSnackbar("再按一次退出编辑")
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
