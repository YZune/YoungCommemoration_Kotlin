package com.suda.yzune.youngcommemoration.event_appwidget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.suda.yzune.youngcommemoration.AppDatabase
import com.suda.yzune.youngcommemoration.bean.EventBean
import com.suda.yzune.youngcommemoration.bean.SingleAppWidgetBean

class AppWidgetConfigureViewModel(application: Application) : AndroidViewModel(application) {

    var selectedEvent: EventBean? = null
    var widgetBean = SingleAppWidgetBean()
    val showList = arrayListOf<EventBean>()
    private val dataBase = AppDatabase.getDatabase(application)
    private val eventDao = dataBase.eventDao()
    private val widgetDao = dataBase.singleWidgetDao()

    suspend fun getAllEvents(): List<EventBean> {
        return eventDao.getAllInThread()
    }

    suspend fun insertWidget() {
        widgetDao.insert(widgetBean)
    }

    suspend fun updateWidget() {
        widgetDao.update(widgetBean)
    }
}