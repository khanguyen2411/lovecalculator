package com.hola360.crushlovecalculator.data.response.imagestore

import com.google.gson.annotations.SerializedName
import com.hola360.crushlovecalculator.data.model.ImageStore

data class DataApps(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("lists")
    val lists: MutableList<ImageStore>,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("sub_url")
    val subUrl: String,
    @SerializedName("total_page")
    val totalPage: Int
)