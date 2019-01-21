package com.suda.yzune.youngcommemoration

import android.annotation.TargetApi
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.crashlytics.android.Crashlytics
import com.suda.yzune.youngcommemoration.utils.PreferenceUtils
import io.fabric.sdk.android.Fabric

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (!BuildConfig.DEBUG && !Fabric.isInitialized()) {
            Fabric.with(this, Crashlytics())
        }
        PreferenceUtils.init(applicationContext)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            var channelId = "schedule_reminder"
//            var channelName = "课程提醒"
//            var importance = NotificationManager.IMPORTANCE_HIGH
//            createNotificationChannel(this, channelId, channelName, importance)
//            channelId = "news"
//            channelName = "公告"
//            importance = NotificationManager.IMPORTANCE_LOW
//            createNotificationChannel(this, channelId, channelName, importance)
//        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context, channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        channel.setShowBadge(true)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}