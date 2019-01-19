package com.suda.yzune.youngcommemoration_kotlin.bean

import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.text.ParseException
import java.util.*

data class EventBean(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var content: String,
    var year: Int,
    var month: Int,
    var day: Int,
    var type: Int,
    var path: String,
    var isFav: Boolean,
    var msg: String,
    @Ignore
    var count: Int = 0
) {
    fun getDescriptionWithDays(): String {
        val description = ""
        when (type) {
            0 -> {
                count = getDaysFromNow()
                "「$content」${count}天"
            }
            1 -> {
                val curYear = Calendar.getInstance().get(Calendar.YEAR)
                var durYears = curYear - year
                count = getDaysFromNow(curYear)
                if (count > 0) {
                    durYears++
                    count = -getDaysFromNow(curYear + 1)
                }
                "${content}的${durYears}岁生日还有${count}天"
            }
            2 -> {
                count = getDaysFromNow()
                if (count > 0) {
                    "${content}已过去${count}天"
                } else {
                    count = -count
                    "${content}还有${count}天"
                }
            }
            3 -> {
                //在农历生日下，存储的date就直接是农历日期
                val lunarBirth = LunarBean(year, month, day, false)
            }
            else -> {

            }
        }
        return description
    }

    @Throws(ParseException::class)
    fun getDaysFromNow(curYear: Int? = null): Int {
        val cal = Calendar.getInstance()
        val todayTimeInMillis = cal.timeInMillis
        if (curYear == null) {
            cal.set(year, month, day)
        } else {
            cal.set(curYear, month, day)
        }
        val dateInMillis = cal.timeInMillis
        val betweenDays = (todayTimeInMillis - dateInMillis) / (1000 * 3600 * 24)
        return betweenDays.toInt()
    }
}