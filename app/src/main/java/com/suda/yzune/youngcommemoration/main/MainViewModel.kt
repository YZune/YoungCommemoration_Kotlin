package com.suda.yzune.youngcommemoration.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.suda.yzune.youngcommemoration.AppDatabase
import com.suda.yzune.youngcommemoration.bean.EventBean

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val showList = arrayListOf<EventBean>()
    var favEvent: EventBean? = null
    private val dataBase = AppDatabase.getDatabase(application)
    private val eventDao = dataBase.eventDao()

    fun getAllEvents(): LiveData<List<EventBean>> {
        return eventDao.getAll()
    }

    fun getFavEvent(): LiveData<EventBean> {
        return eventDao.getFav()
    }

    suspend fun getFavEventInThread(): EventBean? {
        return eventDao.getFavInThread()
    }
}