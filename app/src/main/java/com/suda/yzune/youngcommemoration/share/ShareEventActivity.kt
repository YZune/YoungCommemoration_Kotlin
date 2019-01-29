package com.suda.yzune.youngcommemoration.share

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import com.suda.yzune.youngcommemoration.event_appwidget.AppWidgetConfigureViewModel
import com.suda.yzune.youngcommemoration.main.EventListAdapter
import gdut.bsx.share2.FileUtil
import gdut.bsx.share2.Share2
import gdut.bsx.share2.ShareContentType
import kotlinx.android.synthetic.main.activity_share_event.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar
import java.io.File

class ShareEventActivity : BaseTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_share_event

    private lateinit var viewModel: AppWidgetConfigureViewModel
    private var maskColor: Int = 0

    override fun onSetupSubButton(tvButton1: TextView, tvButton2: TextView) {
        val iconFont = ResourcesCompat.getFont(this, R.font.iconfont)
        tvButton2.textSize = 20f
        tvButton2.typeface = iconFont
        tvButton2.text = "\uE6DE"
        tvButton2.setOnClickListener {
            if (viewModel.selectedEvent == null) {
                it.longSnackbar("你还没有选择事件哦，往下划动看看。")
            } else {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                } else {
                    exportPicture()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AppWidgetConfigureViewModel::class.java)
        maskColor = ContextCompat.getColor(this, R.color.maskBlack)
        initEvent()
        initList()
    }

    private fun exportPicture() {
        launch {
            val screenshot = Bitmap.createBitmap(ll_share.width, ll_share.height, Bitmap.Config.ARGB_8888)
            val c = Canvas(screenshot)
            ll_share.draw(c)
            val task = withContext(Dispatchers.IO) {
                try {
                    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
                    val myDir = if (dir.endsWith(File.separator)) {
                        "${dir}咩咩/"
                    } else {
                        "$dir/咩咩/"
                    }
                    val dirFile = File(myDir)
                    if (!dirFile.exists()) {
                        dirFile.mkdir()
                    }
                    val mImageTime = System.currentTimeMillis()
                    val dateSeconds = mImageTime / 1000
                    val mImageFileName = "pic_$mImageTime.png"
                    val mImageFilePath = myDir + mImageFileName
                    val values = ContentValues()
                    values.put(MediaStore.Images.ImageColumns.DATA, mImageFilePath)
                    values.put(MediaStore.Images.ImageColumns.TITLE, mImageFileName)
                    values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, mImageFileName)
                    values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, mImageTime)
                    values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds)
                    values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds)
                    values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png")
                    values.put(MediaStore.Images.ImageColumns.WIDTH, screenshot.width)
                    values.put(MediaStore.Images.ImageColumns.HEIGHT, screenshot.height)
                    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                    val out = contentResolver.openOutputStream(uri!!)
                    screenshot.compress(Bitmap.CompressFormat.PNG, 100, out)// bitmap转换成输出流，写入文件
                    out?.flush()
                    out?.close()

                    values.clear()
                    values.put(
                        MediaStore.Images.ImageColumns.SIZE,
                        File(mImageFilePath).length()
                    )
                    contentResolver.update(uri, values, null, null)
                    mImageFilePath
                } catch (e: Exception) {
                    null
                }
            }
            if (task != null) {
                longToast("已保存在相册「咩咩」中")
                Share2.Builder(this@ShareEventActivity)
                    .setContentType(ShareContentType.FILE)
                    .setShareFileUri(FileUtil.getFileUri(this@ShareEventActivity, ShareContentType.IMAGE, File(task)))
                    .setTitle("导出并分享")
                    .build()
                    .shareBySystem()
            } else {
                longToast("出现异常>_<\n$task")
            }
        }

    }

    private fun initEvent() {

        ll_with_logo.setOnClickListener {
            s_logo.isChecked = !s_logo.isChecked
        }

        s_logo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ll_logo.visibility = View.VISIBLE
            } else {
                ll_logo.visibility = View.INVISIBLE
            }
        }

        ll_mask_color.setOnClickListener {
            buildColorPickerDialogBuilder()
                .initialColor(maskColor)
                .setPositiveButton("确定") { _, colorInt, _ ->
                    maskColor = colorInt
                    v_share.backgroundColor = colorInt
                }
                .build()
                .show()
        }

        ll_text_color.setOnClickListener {
            buildColorPickerDialogBuilder()
                .initialColor(tv_days.currentTextColor)
                .setPositiveButton("确定") { _, colorInt, _ ->
                    for (i in 0 until ll_info.childCount) {
                        val v = ll_info.getChildAt(i)
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
            rv_events.layoutManager = LinearLayoutManager(this@ShareEventActivity)
            rv_events.adapter = adapter
        }
    }

    private fun setPreviewContent() {
        val description = viewModel.selectedEvent!!.getDescriptionWithDays(this)
        tv_share_event.text = description[0]
        tv_days.text = description[1]
        tv_start_time.text = description[2]
        if (viewModel.selectedEvent!!.msg.isBlank()) {
            tv_msg.visibility = View.GONE
        } else {
            tv_msg.visibility = View.VISIBLE
            tv_msg.text = viewModel.selectedEvent!!.msg
        }
        GlideApp.with(this)
            .load(if (viewModel.selectedEvent!!.path.isBlank()) R.drawable.default_background else viewModel.selectedEvent!!.path)
            .override(600)
            .into(iv_share)
    }

    private fun buildColorPickerDialogBuilder(): ColorPickerDialogBuilder {
        return ColorPickerDialogBuilder
            .with(this)
            .setTitle("选取颜色")
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setNegativeButton("取消") { _, _ -> }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportPicture()
                } else {
                    longToast("你取消了授权，无法分享图片")
                }
            }
        }
    }

}
