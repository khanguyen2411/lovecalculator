package com.hola360.crushlovecalculator.data.model.theme

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel

@Entity(tableName = "tblFavoriteTheme")
data class ThemeModel(
    @PrimaryKey
    @ColumnInfo(name = "item_id")
    @SerializedName("item_id")
    var itemId: String = "",

    @Ignore
    @SerializedName("name")
    val name: String = "",

    @Ignore
    @SerializedName("thumb")
    var thumb: String = "",

    @Ignore
    @SerializedName("full")
    var full: String = "",

    @Ignore
    @SerializedName("zip")
    var zip: String = "",

    @Ignore
    @SerializedName("like")
    val like: Int = -1,

    @Ignore
    @SerializedName("view")
    val view: Int = -1,

    @Ignore
    @SerializedName("download")
    val download: Int = -1
) {
    @Ignore
    @IgnoredOnParcel
    var header = false
}
