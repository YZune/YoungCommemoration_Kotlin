package com.suda.yzune.youngcommemoration.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.suda.yzune.youngcommemoration.GlideApp
import com.zhihu.matisse.engine.ImageEngine

class GlideAppEngine : ImageEngine {
    override fun loadAnimatedGifThumbnail(
        context: Context,
        resize: Int,
        placeholder: Drawable?,
        imageView: ImageView,
        uri: Uri?
    ) {
        GlideApp.with(context)
            .asBitmap()
            .load(uri)
            .placeholder(placeholder)
            .override(resize, resize)
            .centerCrop()
            .into(imageView)
    }

    override fun loadAnimatedGifImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri?) {
        GlideApp.with(context)
            .asGif()
            .load(uri)
            .override(resizeX, resizeY)
            .priority(Priority.HIGH)
            .into(imageView)
    }

    override fun loadThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView, uri: Uri) {
        GlideApp.with(context)
            .asBitmap()
            .load(uri)
            .placeholder(placeholder)
            .override(resize, resize)
            .centerCrop()
            .into(imageView)
    }

    override fun loadImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        GlideApp.with(context)
            .load(uri)
            .override(resizeX, resizeY)
            .priority(Priority.HIGH)
            .into(imageView)
    }

    override fun supportAnimatedGif(): Boolean {
        return true
    }
}