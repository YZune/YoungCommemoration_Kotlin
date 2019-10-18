package com.suda.yzune.youngcommemoration.main

import androidx.core.content.ContextCompat
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.florent37.glidepalette.GlidePalette
import com.suda.yzune.youngcommemoration.R
import com.suda.yzune.youngcommemoration.bean.EventBean
import jp.wasabeef.glide.transformations.BlurTransformation
import org.jetbrains.anko.backgroundColor

class EventListAdapter(layoutResId: Int, data: MutableList<EventBean>) :
    BaseItemDraggableAdapter<EventBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: EventBean) {
        val description = item.getDescriptionWithDays(mContext)
        helper.setText(R.id.tv_content, description[0])
        helper.setText(R.id.tv_count, description[1])
        helper.setText(R.id.tv_date, description[2])

        if (item.msg.isBlank()) {
            helper.getView<View>(R.id.tv_msg).visibility = View.GONE
        } else {
            helper.getView<View>(R.id.tv_msg).visibility = View.VISIBLE
            helper.setText(R.id.tv_msg, item.msg)
        }

        Glide.with(mContext)
            .load(if (item.path.isBlank()) R.drawable.default_background else item.path)
            .override(300)
            .into(helper.getView(R.id.iv_pic))
        Glide.with(mContext)
            .load(if (item.path.isBlank()) R.drawable.default_background else item.path)
            .override(300)
            .apply(bitmapTransform(BlurTransformation(25))).listener(
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