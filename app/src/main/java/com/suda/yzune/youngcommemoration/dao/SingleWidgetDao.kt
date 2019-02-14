package com.suda.yzune.youngcommemoration.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.suda.yzune.youngcommemoration.bean.SingleAppWidgetBean

@Dao
interface SingleWidgetDao {

    @Insert
    fun insert(widget: SingleAppWidgetBean)

    @Update
    fun update(widget: SingleAppWidgetBean)

    @Query("delete from singleappwidgetbean where id = :id")
    fun delete(id: Int)

    @Query("select * from singleappwidgetbean")
    fun getAll(): List<SingleAppWidgetBean>

    @Query("select * from singleappwidgetbean where eventId = :eventId")
    fun getByEvent(eventId: Int): List<SingleAppWidgetBean>
}