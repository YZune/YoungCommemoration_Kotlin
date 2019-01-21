package com.suda.yzune.youngcommemoration.main

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.florent37.glidepalette.GlidePalette
import com.suda.yzune.youngcommemoration.GlideApp
import com.suda.yzune.youngcommemoration.GlideOptions
import com.suda.yzune.youngcommemoration.R
import com.suda.yzune.youngcommemoration.bean.EventBean
import jp.wasabeef.glide.transformations.BlurTransformation
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip

class EventListAdapter(layoutResId: Int, data: MutableList<EventBean>) :
    BaseItemDraggableAdapter<EventBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: EventBean) {
        if (helper.layoutPosition == 0) {
            (helper.getView<View>(R.id.cv_event).layoutParams as LinearLayout.LayoutParams).topMargin = mContext.dip(24)
        }
        val description = item.getDescriptionWithDays()
        helper.setText(R.id.tv_content, description[0])
        helper.setText(R.id.tv_count, description[1])
        helper.setText(R.id.tv_date, description[2])

        if (item.msg.isBlank()) {
            helper.getView<View>(R.id.tv_msg).visibility = View.GONE
        } else {
            helper.getView<View>(R.id.tv_msg).visibility = View.VISIBLE
            helper.setText(R.id.tv_msg, item.msg)
        }

        GlideApp.with(mContext)
            .load(if (item.path.isBlank()) R.drawable.default_background else item.path)
            .override(300)
            .into(helper.getView(R.id.iv_pic))
        GlideApp.with(mContext)
            .load(if (item.path.isBlank()) R.drawable.default_background else item.path)
            .override(300)
            .apply(GlideOptions.bitmapTransform(BlurTransformation(25))).listener(
                GlidePalette
                    .with(item.path)
                    .intoCallBack {
                        val color = it!!.getLightVibrantColor(ContextCompat.getColor(mContext, R.color.blue_50))
                        helper.getView<View>(R.id.v_bg).backgroundColor = color
                    }

            )
            .into(helper.getView(R.id.iv_pic_bg))
    }
}