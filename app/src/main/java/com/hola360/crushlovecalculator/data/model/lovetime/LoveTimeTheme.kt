package com.hola360.crushlovecalculator.data.model.lovetime

import com.google.gson.annotations.SerializedName

data class LoveTimeTheme(
    @SerializedName("theme_color")
    val themeColor: String,
    @SerializedName("name_color")
    val nameColor: String,
    @SerializedName("name_frame_color")
    val frameColor: String,
    @SerializedName("has_big_heart")
    val hasBigHeart: Int,
    @SerializedName("has_name_frame")
    val hasNameFrame: Int,
    @SerializedName("theme_type")
    val themeType: Int
) {
}