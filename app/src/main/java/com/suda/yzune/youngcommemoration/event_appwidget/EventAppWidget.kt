package com.suda.yzune.youngcommemoration.event_appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.bumptech.glide.request.target.AppWidgetTarget
import com.suda.yzune.youngcommemoration.AppDatabase
import com.suda.yzune.youngcommemoration.GlideApp
import com.suda.yzune.youngcommemoration.R
import com.suda.yzune.youngcommemoration.bean.EventBean
import com.suda.yzune.youngcommemoration.bean.SingleAppWidgetBean
import com.suda.yzune.youngcommemoration.main.MainActivity
import kotlinx.coroutines.*

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [EventAppWidgetConfigureActivity]
 */
class EventAppWidget : AppWidgetProvider() {

    private var job: Job? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val dataBase = AppDatabase.getDatabase(context)
        val widgetDao = dataBase.singleWidgetDao()
        val eventDao = dataBase.eventDao()

        job = GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                for (appWidget in widgetDao.getAll()) {
                    val e = eventDao.getByIdInThread(appWidget.eventId)
                    withContext(Dispatchers.Main) {
                        updateAppWidget(context, appWidgetManager, appWidget.id, e, appWidget)
                    }
                }
            }
            job?.cancel()
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val dataBase = AppDatabase.getDatabase(context)
        val widgetDao = dataBase.singleWidgetDao()
        job = GlobalScope.launch(Dispatchers.IO) {
            for (id in appWidgetIds) {
                widgetDao.delete(id)
            }
            job?.cancel()
        }
    }

    companion object {

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int, event: EventBean, widget: SingleAppWidgetBean
        ) {
            val mRemoteViews = RemoteViews(context.packageName, R.layout.event_app_widget)
            val description = event.getDescriptionWithDays()
            mRemoteViews.setTextViewText(R.id.tv_event_widget, description[0])
            mRemoteViews.setTextViewText(R.id.tv_days_widget, description[1])
            if (event.msg.isBlank()) {
                mRemoteViews.setViewVisibility(R.id.tv_event_msg, View.GONE)
            } else {
                mRemoteViews.setViewVisibility(R.id.tv_event_msg, View.VISIBLE)
                mRemoteViews.setTextViewText(R.id.tv_event_msg, event.msg)
            }
            if (widget.withPic) {
                mRemoteViews.setViewVisibility(R.id.iv_widget, View.VISIBLE)
                val appWidgetTarget = AppWidgetTarget(context, R.id.iv_widget, mRemoteViews, appWidgetId)
                GlideApp.with(context)
                    .asBitmap()
                    .load(if (event.path.isBlank()) R.drawable.default_background else event.path)
                    .override(300)
                    .into(appWidgetTarget)
            } else {
                mRemoteViews.setViewVisibility(R.id.iv_widget, View.GONE)
            }
//            if (widget.withShadow) {
//                mRemoteViews.setFloat(R.id.ll_background, "setTranslationZ", context.dip(4).toFloat())
//            } else {
//                mRemoteViews.setFloat(R.id.ll_background, "setTranslationZ", 0f)
//            }
            mRemoteViews.setInt(R.id.ll_background, "setBackgroundColor", widget.bgColor)
            mRemoteViews.setTextColor(R.id.tv_event_widget, widget.textColor)
            mRemoteViews.setTextColor(R.id.tv_days_widget, widget.textColor)
            mRemoteViews.setTextColor(R.id.tv_event_msg, widget.textColor)
            val intent = Intent(context, MainActivity::class.java)
            val pIntent = PendingIntent.getActivity(context, 0, intent, 0)
            mRemoteViews.setOnClickPendingIntent(R.id.ll_background, pIntent)
            appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews)
        }
    }
}

