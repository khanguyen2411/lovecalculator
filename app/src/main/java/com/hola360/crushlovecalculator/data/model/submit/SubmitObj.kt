package com.hola360.crushlovecalculator.data.model.submit

import com.google.gson.annotations.SerializedName

data class SubmitObj(
    @SerializedName("data_apps")
    val submitResult: SubmitResult
)
