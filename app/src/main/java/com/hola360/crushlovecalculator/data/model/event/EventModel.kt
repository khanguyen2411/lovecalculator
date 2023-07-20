package com.hola360.crushlovecalculator.data.model.event

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tblMyEvent")
data class EventModel(
    @ColumnInfo(name = "event_id")
    @PrimaryKey(autoGenerate = true)
    var eventId : Int? = null,

    @SerializedName("title")
    @ColumnInfo(name = "title")
    var title : String = "",

    @ColumnInfo(name = "thumb")
    var thumb : String = "",

    @Ignore
    @SerializedName("bg")
    var background: String = "",

    @Ignore
    @SerializedName("icon")
    var icon: String = "",

    @Ignore
    @SerializedName("theme_color")
    var themeColor: String = "",

    @ColumnInfo(name = "date")
    @SerializedName("even_date")
    var date : Long = -1,

    @ColumnInfo(name = "calculation_type")
    var calType : Int = -1,

    @ColumnInfo(name="created_date")
    var createdDate : Long = -1
) : Parcelable
