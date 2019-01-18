package com.suda.yzune.youngcommemoration_kotlin.bean

import androidx.room.Entity

@Entity(primaryKeys = ["id"])
data class EventBean(
    var id: Int,
    var content: String,
    var type: Int,
    var path: String,
    var isFav: Boolean,
    var msg: String
)