package com.suda.yzune.youngcommemoration.bean

data class EventOldBean(
    var date: String,
    var context: String,
    var type: Int,
    var picture_path: String,
    var isFavourite: Boolean
)