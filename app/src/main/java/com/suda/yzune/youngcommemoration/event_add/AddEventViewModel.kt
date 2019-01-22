package com.suda.yzune.youngcommemoration.event_add

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.suda.yzune.youngcommemoration.AppDatabase
import com.suda.yzune.youngcommemoration.bean.EventBean
import com.suda.yzune.youngcommemoration.bean.SingleAppWidgetBean

class AddEventViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBase = AppDatabase.getDatabase(application)
    private val eventDao = dataBase.eventDao()
    private val widgetDao = dataBase.singleWidgetDao()
    lateinit var event: EventBean
    var isNew = true

    suspend fun getEventWidgets(): List<SingleAppWidgetBean> {
        return widgetDao.getByEvent(event.id)
    }

    suspend fun save() {
        if (event.isFav) {
            val tmp = eventDao.getFavInThread()
            if (tmp != null) {
                tmp.isFav = false
                eventDao.update(tmp)
            }
        }
        if (isNew) {
            eventDao.insert(event)
        } else {
            eventDao.update(event)
        }
    }

    suspend fun delete() {
        eventDao.delete(event)
    }
}