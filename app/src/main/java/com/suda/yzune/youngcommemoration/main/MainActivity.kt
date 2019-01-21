package com.suda.yzune.youngcommemoration.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import com.suda.yzune.youngcommemoration.GlideApp
import com.suda.yzune.youngcommemoration.R
import com.suda.yzune.youngcommemoration.base_view.BaseActivity
import com.suda.yzune.youngcommemoration.event_add.AddEventActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.*


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: EventListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        lightStatusIcon = true
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        setContentView(com.suda.yzune.youngcommemoration.R.layout.activity_main)
        resizeMarginTop()
        initView()
        initEven()
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
                val description = viewModel.favEvent!!.getDescriptionWithDays()
                tv_event.text = description[0]
                tv_days.setContent(viewModel.favEvent!!.count.toString())
                tv_start_time.text = description[2]
                tv_event_main.text = description[0] + " " + description[1]
                GlideApp.with(this@MainActivity)
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

    private fun initView() {
        adapter = EventListAdapter(R.layout.item_event, viewModel.showList)
        adapter.setOnItemClickListener { adapter, view, position ->
            startActivity<AddEventActivity>("event" to viewModel.showList[position])
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
        rv_events.layoutManager = LinearLayoutManager(this)
        rv_events.adapter = adapter
        viewModel.getAllEvents().observe(this, Observer {
            if (it == null) return@Observer
            viewModel.showList.clear()
            viewModel.showList.addAll(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun resizeMarginTop() {
        val statusBarMargin = getStatusBarHeight()
        (toolbar.layoutParams as FrameLayout.LayoutParams).topMargin = statusBarMargin
    }

    private fun initEven() {
        tv_add.setOnClickListener {
            startActivity<AddEventActivity>()
        }

        appbar_layout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val scrollRange = appBarLayout.totalScrollRange
                val alpha = 1 - 1.0f * Math.abs(verticalOffset) / scrollRange
                ll_info.alpha = alpha
                tv_event_main.alpha = 1 - alpha
            })
    }
}
