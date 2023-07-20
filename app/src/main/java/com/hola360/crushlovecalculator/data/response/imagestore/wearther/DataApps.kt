package com.hola360.crushlovecalculator.data.response.imagestore.wearther

import com.hola360.crushlovecalculator.data.model.weather.WeatherModel

data class DataApps(
    val list: List<WeatherModel>,
    val msg: String,
    val status: Int,
    val sub_url: String
)