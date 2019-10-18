package com.suda.yzune.youngcommemoration.bean

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    foreignKeys = [(
            ForeignKey(
                entity = EventBean::class,
                parentColumns = ["id"],
                childColumns = ["eventId"],
                onUpdate = CASCADE,
                onDelete = CASCADE
            ))], primaryKeys = ["id"]
)
data class SingleAppWidgetBean(
    var id: Int = 0,
    var eventId: Int = -1,
    var withPic: Boolean = true,
    var weight: Int = 0,
    var bgColor: Int = 0x9affffff.toInt(),
    var textColor: Int = 0xff000000.toInt(),
    var textHorizontal: Boolean = false,
    var contentSize: Int = 14,
    var daySize: Int = 20,
    var msgSize: Int = 14
) : Parcelable