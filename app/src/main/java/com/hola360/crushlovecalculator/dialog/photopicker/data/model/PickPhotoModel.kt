package com.hola360.crushlovecalculator.dialog.photopicker.data.model

import com.hola360.crushlovecalculator.data.model.PhotoModel

data class PickPhotoModel(
    val photoModelList: MutableList<PhotoModel>,
    val pickPhotoType: PickPhotoType
)
