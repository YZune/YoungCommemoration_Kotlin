package com.suda.yzune.youngcommemoration.bean

data class LunarBean(
    var lunarYear: Int,
    var lunarMonth: Int,
    var lunarDay: Int,
    var isLeap: Boolean
) {
    constructor() : this(0, 0, 0, false)
}