package com.suda.yzune.youngcommemoration_kotlin.bean

data class LunarBean(
    var lunarYear: Int,
    var lunarMonth: Int,
    var lunarDay: Int,
    var isLeap: Boolean
) {
    constructor() : this(0, 0, 0, false)
}