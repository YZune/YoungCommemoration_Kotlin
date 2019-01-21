package com.suda.yzune.youngcommemoration.event_add

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.BaseDialogFragment
import android.view.View
import cn.carbs.android.gregorianlunarcalendar.library.data.ChineseCalendar
import com.suda.yzune.youngcommemoration.R
import kotlinx.android.synthetic.main.fragment_select_day_dialog.*
import java.util.*

class SelectDayDialog : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_select_day_dialog

    private lateinit var viewModel: AddEventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        layoutWidth = 320
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(AddEventViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.event.type == 2) {
            tv_title.text = "选择农历日期"
            val cal = ChineseCalendar()
            cal.set(ChineseCalendar.CHINESE_YEAR, viewModel.event.year)
            cal.set(ChineseCalendar.CHINESE_MONTH, viewModel.event.month + 1)
            cal.set(ChineseCalendar.CHINESE_DATE, viewModel.event.day)
            calendar_view.init(cal, false)
        } else {
            tv_title.text = "选择日期"
            val cal = Calendar.getInstance()
            cal.set(viewModel.event.year, viewModel.event.month, viewModel.event.day)
            calendar_view.init(cal)
        }

        btn_cancel.setOnClickListener { dismiss() }
        btn_save.setOnClickListener {
            if (viewModel.event.type == 2) {
                val cal = calendar_view.calendarData.calendar
                viewModel.event.year = cal.get(ChineseCalendar.CHINESE_YEAR)
                var m = cal.get(ChineseCalendar.CHINESE_MONTH)
                if (m < 0) {
                    m = -m
                }
                viewModel.event.month = m - 1
                viewModel.event.day = cal.get(ChineseCalendar.CHINESE_DATE)
            } else {
                val cal = calendar_view.calendarData.calendar
                viewModel.event.year = cal.get(Calendar.YEAR)
                viewModel.event.month = cal.get(Calendar.MONTH)
                viewModel.event.day = cal.get(Calendar.DATE)
            }
            dismiss()
            (activity!! as AddEventActivity).initView()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SelectDayDialog()
    }
}
