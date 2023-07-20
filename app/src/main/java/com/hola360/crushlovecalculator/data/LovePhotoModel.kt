package com.hola360.crushlovecalculator.data

import android.net.Uri
import java.io.File

class LovePhotoModel {
    var header = false
    var time: Long = 0
    var file: File? = null
    var uri: Uri? = null
    var selectMode = false
    var isSelect = false
    var timeInGroup: String? = null

    constructor(
        time: Long,
        file: File?,
        uri: Uri?,
        timeInGroup: String?
    ) {
        this.time = time
        this.file = file
        this.uri = uri
        this.timeInGroup = timeInGroup
    }

    constructor(header: Boolean, timeInGroup: String?) {
        this.header = header
        this.timeInGroup = timeInGroup
    }


}