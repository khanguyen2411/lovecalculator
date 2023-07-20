package com.hola360.crushlovecalculator.data.model.story

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tblStory")
data class StoryModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "story_id")
    var storyId: Int? = null,

    @ColumnInfo(name = "event_id")
    var eventId: Int = -1,

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "images")
    var images: String = "",

    @ColumnInfo(name = "date")
    var date: Long = -1
) : Parcelable
