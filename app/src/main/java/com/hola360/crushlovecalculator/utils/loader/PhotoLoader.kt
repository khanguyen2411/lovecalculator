package com.hola360.crushlovecalculator.utils.loader

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.PhotoModel
import java.io.File

object PhotoLoader {

    fun loadAllImages(
        context: Context
    ): MutableList<PhotoModel> {
        val uri = getUri()
        val selectionArgs: Array<String?> = arrayOf("image/png", "image/jpg", "image/jpeg")
        val selection =
            MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?"
        return getListFromUri(context, uri, selection, selectionArgs, false)
    }

    private fun getUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Files.getContentUri("external")
        }
    }


    private fun getListFromUri(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArg: Array<String?>?, orderAscending: Boolean
    ): MutableList<PhotoModel> {
        val list = mutableListOf<PhotoModel>()
        val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.RELATIVE_PATH,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID
            )
        } else {
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID
            )
        }
        LoaderUtils.createCursor(
            context.contentResolver,
            uri,
            projection,
            selection,
            selectionArg,
            orderAscending
        ).use { cursor ->
            cursor?.let {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val albumNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val pathColumn =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
                    } else {
                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    }

                while (cursor.moveToNext()) {
                    val path =
                        LoaderUtils.getPathFromCursor(context, cursor, uri, idColumn, pathColumn) ?: continue
                    val file = File(path)
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val albumId =
                        cursor.getLong(albumIdColumn)
                    if (!file.exists() or file.isHidden or file.isDirectory) continue
                    val photoModel = PhotoModel(
                        ContentUris.withAppendedId(
                            uri,
                            id
                        ),
                        file,
                        name ?: context.getString(R.string.unknown),
                        albumId,
                        cursor.getString(albumNameColumn) ?: context.getString(R.string.unknown)

                    )
                    list.add(photoModel)
                }
                cursor.close()
            }

        }
        return list
    }


}