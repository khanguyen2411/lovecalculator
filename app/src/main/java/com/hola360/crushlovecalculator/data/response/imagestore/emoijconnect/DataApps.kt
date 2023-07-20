package com.hola360.crushlovecalculator.data.response.imagestore.emoijconnect

import com.hola360.crushlovecalculator.data.model.EmoijConnect

data class DataApps(
    val list: List<EmoijConnect>,
    val mgs: String,
    val status: Int,
    val sub_url: String
)