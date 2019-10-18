package com.suda.yzune.youngcommemoration.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suda.yzune.youngcommemoration.AppDatabase
import com.suda.yzune.youngcommemoration.bean.EventBean
import java.io.File
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val showList = arrayListOf<EventBean>()
    var favEvent: EventBean? = null
    val sortTypeLiveData = MutableLiveData<Int>()
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

    suspend fun insertEvents(events: List<EventBean>) {
        eventDao.insertList(events)
    }

    suspend fun updateEvents(events: List<EventBean>) {
        eventDao.updateEvents(events)
    }

    suspend fun exportData(currentDir: String): String {
        val myDir = if (currentDir.endsWith(File.separator)) {
            "${currentDir}咩咩/"
        } else {
            "$currentDir/咩咩/"
        }
        val dir = File(myDir)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val gson = Gson()
        val s = gson.toJson(eventDao.getAllInThread())
        val cal = Calendar.getInstance()
        val file = File(
            myDir,
            "备份 ${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DATE)}.young"
        )
        file.writeText(s)
        return file.path
    }

    suspend fun importFromFile(path: String): String {
        val gson = Gson()
        val file = File(path)
        val list = file.readLines()
        val eventList = gson.fromJson<List<EventBean>>(list[0], object : TypeToken<List<EventBean>>() {}.type)
        eventList.forEach {
            it.id = 0
            it.isFav = false
        }
        eventDao.insertList(eventList)
        return "ok"
    }
}