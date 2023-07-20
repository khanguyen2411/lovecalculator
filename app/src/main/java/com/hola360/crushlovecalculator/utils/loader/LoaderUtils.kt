package com.hola360.crushlovecalculator.utils.loader

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi

object LoaderUtils {
    fun getPathFromCursor(
        context: Context,
        cursor: Cursor,
        uri: Uri,
        idColumn: Int,
        pathColumn: Int
    ): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(
                uri,
                id
            )
            PathUtils.getPath(context, contentUri)
        } else {
            cursor.getString(pathColumn)
        }
    }

    fun createCursor(
        contentResolver: ContentResolver,
        collection: Uri,
        projection: Array<String>,
        whereCondition: String?,
        selectionArgs: Array<String?>?, orderAscending: Boolean
    ): Cursor? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            val selection =
                createSelectionBundle(whereCondition, selectionArgs, orderAscending)
            contentResolver.query(collection, projection, selection, null)
        }
        else -> {
            val sortType = if (orderAscending) {
                " ASC "
            } else {
                " DESC "
            }
            val orderBy = MediaStore.Files.FileColumns.DATE_MODIFIED + sortType
            contentResolver.query(collection, projection, whereCondition, selectionArgs, orderBy)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createSelectionBundle(
        whereCondition: String?,
        selectionArgs: Array<String?>?,
        orderAscending: Boolean
    ): Bundle = Bundle().apply {
        putStringArray(
            ContentResolver.QUERY_ARG_SORT_COLUMNS,
            arrayOf(MediaStore.Files.FileColumns.DATE_MODIFIED)
        )
        val orderDirection =
            if (orderAscending) ContentResolver.QUERY_SORT_DIRECTION_ASCENDING else ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
        putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, orderDirection)
        putString(ContentResolver.QUERY_ARG_SQL_SELECTION, whereCondition)
        putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
    }
}