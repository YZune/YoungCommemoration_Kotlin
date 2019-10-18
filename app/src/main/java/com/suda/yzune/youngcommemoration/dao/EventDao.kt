package com.suda.yzune.youngcommemoration.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.suda.yzune.youngcommemoration.bean.EventBean

@Dao
interface EventDao {

    @Insert
    fun insert(event: EventBean)

    @Update
    fun update(event: EventBean)

    @Update
    fun updateEvents(events: List<EventBean>)

    @Delete
    fun delete(event: EventBean)

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

    @Insert
    fun insertList(events: List<EventBean>)

}