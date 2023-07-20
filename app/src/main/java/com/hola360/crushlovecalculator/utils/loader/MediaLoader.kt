package com.hola360.crushlovecalculator.utils.loader

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.hola360.crushlovecalculator.data.LovePhotoModel
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.SystemUtils.isAndroidQ
import com.hola360.crushlovecalculator.utils.Utils.timeInGroup
import java.io.File
import java.util.*

object MediaLoader {
    fun getAllLovePhoto(
        context: Context, isOldestDateFirst: Boolean, isDetail: Boolean
    ): MutableList<LovePhotoModel> {
        val uri = getImageUri()
        val args: Array<String?>?
        val parentFolder =
            "%" + Environment.DIRECTORY_PICTURES + File.separator + Constants.PHOTO_PATH + "%"
        val whereLike: String = if (isAndroidQ()) {
            MediaStore.Files.FileColumns.RELATIVE_PATH + " LIKE ? "
        } else {
            MediaStore.Files.FileColumns.DATA + " LIKE ? "
        }
        args = arrayOf(parentFolder)
        return getListFromUri(context, uri, whereLike, args, isOldestDateFirst, isDetail)
    }

    private fun getImageUri(): Uri {
        return if (isAndroidQ()) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }


    private fun getListFromUri(
        context: Context,
        uri: Uri,
        selection: String,
        selectionArg: Array<String?>?, isOldestDateFirst: Boolean, isDetail: Boolean
    ): ArrayList<LovePhotoModel> {
        val list = ArrayList<LovePhotoModel>()
        val projection: Array<String> = if (isAndroidQ()) {
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.RELATIVE_PATH,
                MediaStore.MediaColumns.DATE_MODIFIED
            )
        } else {
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DATE_MODIFIED
            )
        }
        val cursor = LoaderUtils.createCursor(
            context.contentResolver,
            uri,
            projection,
            selection,
            selectionArg, isOldestDateFirst
        )
        if (cursor != null) {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val dateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)
            val pathColumn: Int = if (isAndroidQ()) {
                cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
            } else {
                cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            }
            val nowCalendar = Calendar.getInstance()
            val fileCalendar = Calendar.getInstance()
            while (cursor.moveToNext()) {
                val path = LoaderUtils.getPathFromCursor(context, cursor, uri, idColumn, pathColumn)
                val time = cursor.getLong(dateModifiedColumn)
                if (path==null){
                    continue
                }
                val file = File(path)
                if (!file.exists() || file.isHidden) {
                    continue
                }
                val id = cursor.getLong(idColumn)
                val timeInGroup = if (isDetail) {
                    null
                } else {
                    fileCalendar.timeInMillis = time * 1000L
                    timeInGroup(context, nowCalendar, fileCalendar)
                }
                val localSongModel = LovePhotoModel(
                    time, file, ContentUris.withAppendedId(
                        uri,
                        id
                    ), timeInGroup
                )
                list.add(localSongModel)
            }
            cursor.close()
        }
        return list
    }

//    private fun getPathFromCursor(
//        context: Context,
//        cursor: Cursor,
//        uri: Uri?,
//        idColumn: Int,
//        pathColumn: Int
//    ): String {
//        return if (isAndroidQ()) {
//            val id = cursor.getLong(idColumn)
//            val contentUri = ContentUris.withAppendedId(
//                uri!!,
//                id
//            )
//            PathUtils.getPath(context, contentUri)!!
//        } else {
//            cursor.getString(pathColumn)
//        }
//    }
//
//    private fun createCursor(
//        context: Context,
//        collection: Uri,
//        projection: Array<String>,
//        whereCondition: String,
//        selectionArgs: Array<String>, isOldestDateFirst: Boolean
//    ): Cursor? {
//        return if (isAndroidQ()) {
//            val selection = createSelectionBundle(whereCondition, selectionArgs, isOldestDateFirst)
//            context.contentResolver.query(collection, projection, selection, null)
//        } else {
//            val sortType = if (isOldestDateFirst) {
//                " ASC "
//            } else {
//                " DESC "
//            }
//            val orderBy = MediaStore.Files.FileColumns.DATE_MODIFIED + sortType
//            context.contentResolver.query(
//                collection,
//                projection,
//                whereCondition,
//                selectionArgs,
//                orderBy
//            )
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private fun createSelectionBundle(
//        whereCondition: String,
//        selectionArgs: Array<String>, isOldestDateFirst: Boolean
//    ): Bundle {
//        val bundle = Bundle()
//        bundle.putStringArray(
//            ContentResolver.QUERY_ARG_SORT_COLUMNS,
//            arrayOf(MediaStore.Files.FileColumns.DATE_MODIFIED)
//        )
//        val orderDirection = if (isOldestDateFirst) {
//            ContentResolver.QUERY_SORT_DIRECTION_ASCENDING;
//        } else {
//            ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
//        }
//        bundle.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, orderDirection)
//        bundle.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, whereCondition)
//        bundle.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
//        return bundle
//    }
}