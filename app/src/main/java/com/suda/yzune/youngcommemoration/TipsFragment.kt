package com.suda.yzune.youngcommemoration

import android.os.Build
import android.os.Bundle
import androidx.core.text.HtmlCompat
import android.text.Html
import android.text.Spanned
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import com.suda.yzune.youngcommemoration.utils.PreferenceUtils
import kotlinx.android.synthetic.main.fragment_tips.*

class TipsFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_tips

    private var tips = ""
    private var key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tips = it.getString("tips", "")
            key = it.getString("key", "")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_info.text = getHtmlSpannedString(tips)
        tv_visit.setOnClickListener {
            PreferenceUtils.saveBooleanToSP(activity!!, key, true)
            dismiss()
        }
        ib_close.setOnClickListener {
            dismiss()
        }
    }

    private fun getHtmlSpannedString(str: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(str, HtmlCompat.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(str)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: String, arg1: String) =
            TipsFragment().apply {
                arguments = Bundle().apply {
                    putString("tips", arg)
                    putString("key", arg1)
                }
            }
    }
}
