package com.hola360.crushlovecalculator.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.IgnoredOnParcel


@Entity(indices = [Index(value = ["itemId"], unique = true)])
@Parcelize
class ImageStore(
    @PrimaryKey(autoGenerate = true)
    val _id: Long,
    @ColumnInfo(name = "itemId", )
    @SerializedName("item_id")
    val itemId: String?,
    @ColumnInfo(name = "thumb")
    var thumb: String?,
    @ColumnInfo(name = "full")
    var full: String?,
    @ColumnInfo(name = "like")
    var like: Int,
    @ColumnInfo(name = "view")
    var view: Int,
    @ColumnInfo(name = "download")
    var download: Int
) : Parcelable {
    @IgnoredOnParcel
    var header = false
    @IgnoredOnParcel
    var favorite = false
}