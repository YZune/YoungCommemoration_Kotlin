package com.suda.yzune.youngcommemoration.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suda.yzune.youngcommemoration.AppDatabase
import com.suda.yzune.youngcommemoration.bean.EventBean
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import java.io.File
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val showList = arrayListOf<EventBean>()
    var favEvent: EventBean? = null
    val sortTypeLiveData = MutableLiveData<Int>()
    val json = Json(configuration = JsonConfiguration.Stable.copy(strictMode = false))
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
        val s = json.stringify(EventBean.serializer().list, eventDao.getAllInThread())
        val cal = Calendar.getInstance()
        val file = File(
            myDir,
            "备份 ${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DATE)}.young"
        )
        file.writeText(s)
        return file.path
    }

    suspend fun importFromFile(path: String): String {
        val file = File(path)
        val list = file.readLines()
        val eventList = json.parse(EventBean.serializer().list, list[0])
        eventList.forEach {
            it.id = 0
            it.isFav = false
        }
        eventDao.insertList(eventList)
        return "ok"
    }
}