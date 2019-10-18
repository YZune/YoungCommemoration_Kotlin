package com.suda.yzune.youngcommemoration.bean

data class SolarBean(
    var solarYear: Int = 2019,
    var solarMonth: Int = 0,
    var solarDay: Int = 1
) {

    constructor(date: String) : this() {
        val list = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        solarYear = Integer.parseInt(list[0])
        solarMonth = Integer.parseInt(list[1])
        solarDay = Integer.parseInt(list[2])
    }

    override fun toString(): String {
        return "$solarYear-$solarMonth-$solarDay"
    }
}