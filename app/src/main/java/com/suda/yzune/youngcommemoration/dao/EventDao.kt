package com.suda.yzune.youngcommemoration.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.suda.yzune.youngcommemoration.bean.EventBean

@Dao
interface EventDao {

    @Insert
    fun insert(event: EventBean)

    @Update
    fun update(event: EventBean)

    @Query("select * from eventbean")
    fun getAll(): LiveData<List<EventBean>>

    @Query("select * from eventbean")
    fun getAllInThread(): List<EventBean>

    @Query("select * from eventbean where isFav")
    fun getFav(): LiveData<EventBean>

    @Query("select * from eventbean where isFav")
    fun getFavInThread(): EventBean?

    @Query("select * from eventbean where id = :id")
    fun getByIdInThread(id: Int): EventBean
}