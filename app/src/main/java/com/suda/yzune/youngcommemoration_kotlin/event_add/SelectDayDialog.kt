package com.suda.yzune.youngcommemoration_kotlin.event_add


import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.youngcommemoration_kotlin.R
import kotlinx.android.synthetic.main.fragment_select_day_dialog.*

class SelectDayDialog : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_select_day_dialog

    private lateinit var viewModel: AddEventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(AddEventViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar_view.init()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SelectDayDialog()
    }
}
