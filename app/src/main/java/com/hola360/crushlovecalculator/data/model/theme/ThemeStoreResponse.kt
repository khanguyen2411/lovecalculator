package com.hola360.crushlovecalculator.data.model.theme

import com.google.gson.annotations.SerializedName

data class ThemeStoreResponse(
    @SerializedName("data")
    val data: ThemeDataAppsResponse
)