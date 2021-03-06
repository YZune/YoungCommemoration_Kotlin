package com.suda.yzune.youngcommemoration.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import com.google.android.material.appbar.AppBarLayout
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import com.suda.yzune.youngcommemoration.*
import com.suda.yzune.youngcommemoration.base_view.BaseActivity
import com.suda.yzune.youngcommemoration.bean.EventBean
import com.suda.yzune.youngcommemoration.bean.EventOldBean
import com.suda.yzune.youngcommemoration.bean.SolarBean
import com.suda.yzune.youngcommemoration.bean.UpdateInfoBean
import com.suda.yzune.youngcommemoration.event_add.AddEventActivity
import com.suda.yzune.youngcommemoration.share.ShareEventActivity
import com.suda.yzune.youngcommemoration.utils.LunarUtils
import com.suda.yzune.youngcommemoration.utils.MyRetrofitUtils
import com.suda.yzune.youngcommemoration.utils.PreferenceUtils
import com.suda.yzune.youngcommemoration.utils.UpdateUtils.getVersionCode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonException
import kotlinx.serialization.list
import okhttp3.ResponseBody
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.abs


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: EventListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        lightStatusIcon = true
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        updateFromOldVersion()
        setContentView(R.layout.activity_main)
        resizeMarginTop()
        initView()
        initEvent()
        initNavView()
        MyRetrofitUtils.instance.getService().getUpdateInfo().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {}

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response!!.body() != null) {
                    try {
                        val updateInfo = viewModel.json.parse(
                            UpdateInfoBean.serializer(),
                            response.body()!!.string()
                        )
                        if (updateInfo.id > getVersionCode(this@MainActivity.applicationContext)) {
                            UpdateFragment.newInstance(updateInfo).show(supportFragmentManager, "update")
                        }
                    } catch (e: JsonException) {

                    }
                }
            }
        })

        if (!PreferenceUtils.getBooleanFromSP(this, "huawei_appwidget", false)) {
            TipsFragment.newInstance(
                "从旧版本升级上来的<b><font color='#1976D2'>华为用户</font></b>，即使重新放置小部件可能也无法正常显示，请在<b><font color='#1976D2'>侧栏备份</font></b>，然后卸载App重新安装，再还原数据，这样应该能解决小部件的显示问题哦。",
                "huawei_appwidget"
            )
                .show(supportFragmentManager, null)
        }
        viewModel.sortTypeLiveData.value = PreferenceUtils.getIntFromSP(this@MainActivity, "sortType", 0)
    }

    override fun onStart() {
        super.onStart()
        launch {
            viewModel.favEvent = withContext(Dispatchers.IO) {
                viewModel.getFavEventInThread()
            }
            if (viewModel.favEvent == null) {
                tv_event.text = "置顶事件：你最想\n把什么放在这里"
                tv_event_main.text = "你最想把什么放在这里"
                ll_days.visibility = View.GONE
                tv_start_time.visibility = View.GONE
            } else {
                ll_days.visibility = View.VISIBLE
                tv_start_time.visibility = View.VISIBLE
                val description = viewModel.favEvent!!.getDescriptionWithDays(this@MainActivity)
                tv_event.text = description[0]
                if (viewModel.favEvent!!.type == 0 && PreferenceUtils.getBooleanFromSP(
                        this@MainActivity,
                        "s_day_plus",
                        false
                    )
                ) {
                    tv_plus.visibility = View.VISIBLE
                    tv_days.setContent("${viewModel.favEvent!!.count + 1}")
                } else {
                    tv_plus.visibility = View.GONE
                    tv_days.setContent("${viewModel.favEvent!!.count}")
                }
                tv_start_time.text = description[2]
                tv_event_main.text = description[0] + " " + description[1]
                Glide.with(this@MainActivity)
                    .load(if (viewModel.favEvent!!.path.isBlank()) com.suda.yzune.youngcommemoration.R.drawable.default_background else viewModel.favEvent!!.path)
                    .override(600)
                    .listener(
                        GlidePalette
                            .with(viewModel.favEvent!!.path)
                            .use(BitmapPalette.Profile.MUTED_DARK)
                            .intoBackground(v_fav_bg)
                    )
                    .into(iv_fav_bg)
            }
        }
    }

    private fun updateFromOldVersion() {
        val eventJson = PreferenceUtils.getStringFromSP(applicationContext, "events", "")
        if (eventJson != "") {
            val oldList = viewModel.json.parse(EventOldBean.serializer().list, eventJson!!)
            val newList = arrayListOf<EventBean>()
            oldList.forEach {
                val d = it.date.split('-')
                val mYear = d[0].toInt()
                val mMonth = d[1].toInt()
                val mDay = d[2].toInt()
                val newEvent = EventBean().apply {
                    this.content = it.context
                    this.path = it.picture_path
                    this.isFav = it.isFavourite
                }
                when (it.type) {
                    // 农历生日，对应新的2
                    3 -> {
                        val solar = SolarBean(mYear, mMonth, mDay)
                        val lunar = LunarUtils.solarToLunar(solar)
                        newEvent.year = lunar.lunarYear
                        newEvent.month = lunar.lunarMonth - 1
                        newEvent.day = lunar.lunarDay
                        newEvent.type = 2
                        newList.add(newEvent)
                    }
                    2 -> {
                        newEvent.year = mYear
                        newEvent.month = mMonth - 1
                        newEvent.day = mDay
                        newEvent.type = 3
                        newList.add(newEvent)
                    }
                    else -> {
                        newEvent.year = mYear
                        newEvent.month = mMonth - 1
                        newEvent.day = mDay
                        newEvent.type = it.type
                        newList.add(newEvent)
                    }
                }
            }
            launch {
                val msg = withContext(Dispatchers.IO) {
                    try {
                        viewModel.insertEvents(newList)
                        "ok"
                    } catch (e: Exception) {
                        "发生异常>_<" + e.message
                    }
                }
                if (msg == "ok") {
                    PreferenceUtils.remove(applicationContext, "events")
                    TipsFragment.newInstance(
                        "发现你是由旧版本升级上来的哦，注意你从旧版本放置在桌面的<b><font color='#1976D2'>小部件全部失效</font></b>请重新放置。<br>另外为了小部件正常工作，请<b><font color='#1976D2'>允许App保持后台，加入节电白名单</font></b>。",
                        "v1.998"
                    )
                        .show(supportFragmentManager, "tips")
                } else {
                    longToast(msg)
                }
            }

        }
    }

    private fun initNavView() {
        navigation_view.itemIconTintList = null
        navigation_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_about -> {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    drawer_layout.postDelayed({
                        startActivity<AboutActivity>()
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_feedback -> {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    drawer_layout.postDelayed({
                        val c = Calendar.getInstance()
                        val hour = c.get(Calendar.HOUR_OF_DAY)
                        if (hour !in 8..21) {
                            longToast("开发者在休息哦(～﹃～)~zZ请换个时间反馈吧")
                        } else {
                            if (isQQClientAvailable(applicationContext)) {
                                val qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=1055614742&version=1"
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)))
                            } else {
                                longToast("手机上没有安装QQ，无法启动聊天窗口:-(")
                            }
                        }
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_setting -> {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    drawer_layout.postDelayed({
                        startActivity<SettingsActivity>()
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_backup -> {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    drawer_layout.postDelayed({
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1
                            )
                        } else {
                            BackupFragment.newInstance().show(supportFragmentManager, null)
                        }
                    }, 360)
                    return@setNavigationItemSelectedListener true
                }
                else -> {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
            }
        }
    }

    private fun initView() {
        adapter = EventListAdapter(R.layout.item_event, viewModel.showList)
        adapter.setOnItemClickListener { _, _, position ->
            startActivity<AddEventActivity>("event" to viewModel.showList[position])
        }
        adapter.addHeaderView(UI {
            view {
                minimumHeight = dip(24)
            }
        }.view)
        adapter.addFooterView(UI {
            view {
                minimumHeight = dip(56)
            }
        }.view)
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
        rv_events.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rv_events.adapter = adapter
        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(rv_events)
        val onItemDragListener = object : OnItemDragListener {

            override fun onItemDragMoving(
                source: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                from: Int,
                target: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                to: Int
            ) {
            }

            override fun onItemDragStart(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, pos: Int) {
                if (viewModel.sortTypeLiveData.value != 1) {
                    contentView!!.longSnackbar("在当前的排序方式下更改不会保存哦，请在右上角切换")
                }
            }

            override fun onItemDragEnd(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, pos: Int) {
                if (viewModel.sortTypeLiveData.value == 1) {
                    launch {
                        progress_bar.visibility = View.VISIBLE
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )
                        val task = withContext(Dispatchers.IO) {
                            try {
                                viewModel.showList.forEachIndexed { index, eventBean ->
                                    eventBean.sortNum = index
                                }
                                viewModel.updateEvents(viewModel.showList)
                                "ok"
                            } catch (e: Exception) {
                                "发生异常>_<" + e.message
                            }
                        }
                        if (task != "ok") {
                            longToast(task)
                        }
                        progress_bar.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }
            }
        }
        adapter.enableDragItem(itemTouchHelper)
        adapter.setOnItemDragListener(onItemDragListener)
        viewModel.getAllEvents().observe(this, Observer { list ->
            if (list == null) return@Observer
            viewModel.showList.clear()
            viewModel.showList.addAll(list)
            when (viewModel.sortTypeLiveData.value) {
                0 -> viewModel.showList.sortBy { it.id }
                1 -> viewModel.showList.sortBy { it.sortNum }
                2 -> viewModel.showList.sortBy { abs(it.count) }
                3 -> viewModel.showList.sortByDescending { abs(it.count) }
                4 -> viewModel.showList.sortByDescending { it.id }
            }
            adapter.notifyDataSetChanged()
        })
        viewModel.sortTypeLiveData.observe(this, Observer { i ->
            if (i == null) return@Observer
            when (i) {
                0 -> viewModel.showList.sortBy { it.id }
                1 -> viewModel.showList.sortBy { it.sortNum }
                2 -> viewModel.showList.sortBy { abs(it.count) }
                3 -> viewModel.showList.sortByDescending { abs(it.count) }
                4 -> viewModel.showList.sortByDescending { it.id }
            }
            adapter.notifyDataSetChanged()
            PreferenceUtils.saveIntToSP(this, "sortType", i)
        })
    }

    private fun resizeMarginTop() {
        val statusBarMargin = getStatusBarHeight()
        (toolbar.layoutParams as FrameLayout.LayoutParams).topMargin = statusBarMargin
    }

    private fun initEvent() {
        tv_nav.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }

        tv_add.setOnClickListener {
            startActivity<AddEventActivity>()
        }

        tv_share.setOnClickListener {
            startActivity<ShareEventActivity>()
        }

        tv_sort.setOnClickListener {
            SortFragment().show(supportFragmentManager, null)
        }

        appbar_layout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val scrollRange = appBarLayout.totalScrollRange
                val alpha = 1 - 1.0f * Math.abs(verticalOffset) / scrollRange
                ll_info.alpha = alpha
                tv_event_main.alpha = 1 - alpha
            })
    }

    private fun isQQClientAvailable(context: Context): Boolean {
        val packageManager = context.packageManager
        val pInfo = packageManager.getInstalledPackages(0)
        if (pInfo != null) {
            for (i in pInfo.indices) {
                val pn = pInfo[i].packageName
                if (pn.equals("com.tencent.qqlite", ignoreCase = true)
                    || pn.equals("com.tencent.mobileqq", ignoreCase = true)
                    || pn.equals("com.tencent.tim", ignoreCase = true)
                ) {
                    return true
                }
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    BackupFragment().show(supportFragmentManager, null)
                } else {
                    this.longToast("你取消了授权>_<无法备份还原")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val filePath = data!!.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)
            launch {
                val import = withContext(Dispatchers.IO) {
                    try {
                        viewModel.importFromFile(filePath)
                    } catch (e: Exception) {
                        e.message
                    }
                }
                when (import) {
                    "ok" -> {
                        toast("导入成功(ﾟ▽ﾟ)/")
                    }
                    else -> longToast("发生异常>_<\n$import")
                }
            }
        }
    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
            else -> super.onBackPressed()
        }
    }
}
