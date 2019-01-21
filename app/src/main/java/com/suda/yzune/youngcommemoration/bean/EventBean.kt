package com.suda.yzune.youngcommemoration.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.suda.yzune.youngcommemoration.utils.LunarUtils
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity
data class EventBean(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var content: String,
    var year: Int,
    var month: Int, // 注意1月对应的值为0
    var day: Int,
    var type: Int,
    var path: String,
    var isFav: Boolean,
    var msg: String,
    @Ignore
    var count: Int = 0
) : Parcelable {

    constructor() : this(0, "", 0, 0, 0, 0, "", false, "")

    constructor(
        id: Int,
        content: String,
        year: Int,
        month: Int,
        day: Int,
        type: Int,
        path: String,
        isFav: Boolean,
        msg: String
    ) : this(id, content, year, month, day, type, path, isFav, msg, 0)

    fun getDescriptionWithDays(): Array<String> {
        if (year == 0) {
            val cal = Calendar.getInstance()
            year = cal.get(Calendar.YEAR)
            month = cal.get(Calendar.MONTH)
            day = cal.get(Calendar.DATE)
        }
        return when (type) {
            0 -> {
                count = getDaysFromNow()
                arrayOf("「$content」", "$count 天", "从 $year - ${month + 1} - $day")
            }
            1 -> {
                val curYear = Calendar.getInstance().get(Calendar.YEAR)
                var durYears = curYear - year
                count = getDaysFromNow(curYear)
                if (count > 0) {
                    durYears++
                    count = -getDaysFromNow(curYear + 1)
                } else {
                    count = -count
                }
                arrayOf("${content}的 $durYears 岁生日", "还有 $count 天", "生日：$year - ${month + 1} - $day")
            }
            3 -> {
                count = getDaysFromNow()
                if (count > 0) {
                    arrayOf("${content}已过去", "$count 天", "时间：$year - ${month + 1} - $day")
                } else {
                    count = -count
                    arrayOf("${content}还有", "$count 天", "时间：$year - ${month + 1} - $day")
                }
            }
            2 -> {
                val cal = Calendar.getInstance()
                //在农历生日下，存储的date就直接是农历日期
                val lunarBirth = LunarBean(year, month + 1, day, false)
                val lunarNow =
                    LunarBean(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE), false)
                var durYears = lunarNow.lunarYear - lunarBirth.lunarYear
                lunarBirth.lunarYear = lunarNow.lunarYear
                var solarBirth = LunarUtils.lunarToSolar(lunarBirth)
                count = getDaysFromNow(solarBirth.solarYear, solarBirth.solarMonth - 1, solarBirth.solarDay)
                if (count > 0) {
                    durYears++
                    lunarBirth.lunarYear = lunarNow.lunarYear + 1
                    solarBirth = LunarUtils.lunarToSolar(lunarBirth)
                    count = -getDaysFromNow(solarBirth.solarYear, solarBirth.solarMonth - 1, solarBirth.solarDay)
                } else {
                    count = -count
                }
                arrayOf(
                    "${content}的 $durYears 岁农历生日", "还有 $count 天",
                    "对应新历：${solarBirth.solarYear} - ${solarBirth.solarMonth} - ${solarBirth.solarDay}"
                )
            }
            else -> {
                arrayOf("", "")
            }
        }
    }

    fun getDaysFromNow(mYear: Int? = null, mMonth: Int? = null, mDay: Int? = null): Int {
        val cal = Calendar.getInstance()
        val todayTimeInMillis = cal.timeInMillis
        when {
            mYear == null -> cal.set(year, month, day)
            mMonth == null -> cal.set(mYear, month, day)
            else -> cal.set(mYear, mMonth, mDay!!)
        }
        val dateInMillis = cal.timeInMillis
        val betweenDays = (todayTimeInMillis - dateInMillis) / (1000 * 3600 * 24)
        return betweenDays.toInt()
    }
}