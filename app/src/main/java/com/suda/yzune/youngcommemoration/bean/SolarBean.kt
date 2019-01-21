package com.suda.yzune.youngcommemoration.bean

class SolarBean {
    var solarYear: Int = 0
    var solarMonth: Int = 0
    var solarDay: Int = 0

    constructor()

    constructor(y: Int, m: Int, d: Int) {
        solarYear = y
        solarMonth = m
        solarDay = d
    }

    constructor(date: String) {
        val list = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        solarYear = Integer.parseInt(list[0])
        solarMonth = Integer.parseInt(list[1])
        solarDay = Integer.parseInt(list[2])
    }

    override fun toString(): String {
        return "$solarYear-$solarMonth-$solarDay"
    }
}