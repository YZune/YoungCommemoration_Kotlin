package com.suda.yzune.youngcommemoration.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object CountUtils {

    //今天 - date
    @Throws(ParseException::class)
    fun getDaysFromNow(date: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val todayTime = sdf.format(Date())// 获取当前的日期
        val cal = Calendar.getInstance()

        cal.time = sdf.parse(date)
        val time1 = cal.timeInMillis
        cal.time = sdf.parse(todayTime)
        val time2 = cal.timeInMillis
        val betweenDays = (time2 - time1) / (1000 * 3600 * 24)
        return betweenDays.toInt()
    }

}