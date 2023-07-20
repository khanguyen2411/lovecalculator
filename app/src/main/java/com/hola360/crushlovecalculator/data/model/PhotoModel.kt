package com.hola360.crushlovecalculator.data.model

import android.net.Uri
import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class PhotoModel(
    var uri: Uri = Uri.parse(""),
    var file: File = File(""),
    @Expose var title: String = "",
    @Expose var albumId: Long = -1,
    @Expose var albumName: String? = "",
    @Expose var isNormalItem: Boolean = true,
    @Expose var count: Int = 0,
    var isAddMore: Boolean = false,
    @Expose var uriString: String = uri.toString(),
    @Expose var filePath: String = file.absolutePath,
    @Expose var isSelected: Boolean = false
) : Parcelable {
    @Expose
    var childCount: Int = 0
    @Expose
    var isChecked = false
}
