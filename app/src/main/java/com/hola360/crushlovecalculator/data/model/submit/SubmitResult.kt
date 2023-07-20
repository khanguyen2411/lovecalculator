package com.hola360.crushlovecalculator.data.model.submit

import com.google.gson.annotations.SerializedName

data class SubmitResult(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("update_type")
    val updateType: String,
    @SerializedName("item_id")
    val itemId: String,
    @SerializedName("current_count")
    val currentCount: Int
)
