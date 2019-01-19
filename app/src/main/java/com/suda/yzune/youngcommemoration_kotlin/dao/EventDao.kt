package com.suda.yzune.youngcommemoration_kotlin.dao

import androidx.room.Dao
import androidx.room.Insert
import com.suda.yzune.youngcommemoration_kotlin.bean.EventBean

@Dao
interface EventDao {

    @Insert
    fun insert(event: EventBean)

}