package com.hola360.crushlovecalculator.data.model.lovetime

import com.caverock.androidsvg.SVG
import com.hola360.crushlovecalculator.data.model.ProfileModel
import java.io.File

class LoveTimeModel {
    var startDate: Long = -1
    var theme: LoveTimeTheme? = null
    var bigHeartSvg: SVG?=null
    var emojiSvg: SVG?=null
    var bgPath: String?=null
    var avatarFile: File?=null
    var profileModel: ProfileModel?=null
    var crushProfile: ProfileModel?=null

}
