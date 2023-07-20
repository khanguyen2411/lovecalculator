package com.hola360.crushlovecalculator.data.model.diary

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tblDiary")
data class DiaryModel @JvmOverloads constructor(
    @ColumnInfo(name = "diary_id")
    @PrimaryKey(autoGenerate = true)
    var diaryId: Int? = null,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "images")
    var images: String = "",

    @ColumnInfo(name = "time")
    var time: Long = -1,

    @Ignore
    var isDate: Boolean = false,

    @Ignore
    var date: String = ""
) : Parcelable
