package com.hola360.crushlovecalculator.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import android.provider.BaseColumns
import android.provider.MediaStore
import android.widget.Toast
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.Gallery
import com.hola360.crushlovecalculator.ui.myphoto.MyPhotoFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object PhotoUtils {

    @JvmStatic
    fun savePhoto(context: Context, folderName:String, bitmap: Bitmap): Gallery?{
        val dirPath: String = if(SystemUtils.isAndroidQ()){
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.path.plus(File.separator)
                .plus(folderName)
        }else{
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path.plus(File.separator)
                .plus(folderName)
        }
        val rootFolder = File(dirPath)
        if(!rootFolder.exists()){
            rootFolder.mkdirs()
        }

        try {
            val sdf = SimpleDateFormat("MM-dd-yyyy - (hh.mm.a)", Locale.US)
            val fileName =
                context.getString(R.string.app_name).plus(" - [" + sdf.format(Date()) + "]")
            val now = System.currentTimeMillis()
            val quality = 100
            val fos: OutputStream
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues()
                contentValues.put(MediaStore.Images.Media.TITLE, fileName)
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                contentValues.put(
                    MediaStore.Images.Media.DESCRIPTION,
                    context.getString(R.string.app_name)
                )
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                contentValues.put(MediaStore.Images.Media.DATE_ADDED,  now)
                contentValues.put(MediaStore.Images.Media.DATE_TAKEN, now)
                contentValues.put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    (Environment.DIRECTORY_PICTURES).plus(File.separator)
                        .plus(folderName)
                )
                val imageUri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                fos = context.contentResolver.openOutputStream(Objects.requireNonNull(imageUri!!))!!
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos)
                fos.close()
                return getGalleryFromURI(context, imageUri)
            } else {
                val mPath =
                    (rootFolder.absolutePath).plus(File.separator).plus(now).plus(".jpg")
                fos = FileOutputStream(mPath)
                val imageFile = File(mPath)
                if (!imageFile.exists()) {
                    imageFile.createNewFile()
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos)
                fos.close()
                val uri = addImageToGallery(context, fileName, imageFile)
                val mediaFops= context.contentResolver.openOutputStream(uri!!)
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, mediaFops)
                return getGalleryFromURI(context, uri!!)
            }
        }catch (ex:Exception){
            return null
        }
    }

    @SuppressLint("Range")
    private fun getGalleryFromURI(context: Context, contentURI: Uri): Gallery {
        val projection = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.DATE_ADDED
        )
        val cursor: Cursor =
            context.contentResolver.query(contentURI, projection, null, null, null)!!
        cursor.moveToFirst()
        return if (SystemUtils.isAndroidQ()) {
            val title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE))
            val itemId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
            val dateAdded= cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
            val path = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, itemId).toString()
            cursor.close()
            Gallery(itemId, path, contentURI, dateAdded, title)
        } else {
            val title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE))
            val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
            val itemId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            val dateAdded= cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
            cursor.close()
            Gallery(itemId, path, contentURI, dateAdded, title)
        }
    }

    private fun addImageToGallery(context: Context, title: String, filepath: File): Uri? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title)
        values.put(MediaStore.Images.Media.DESCRIPTION, context.getString(R.string.app_name))
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.DATA, filepath.toString())
        return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }




    @JvmStatic
    fun shareSinglePhoto(activity: Activity, gallery: Gallery) {
        val sharingIntent = Intent().apply {
            action= Intent.ACTION_SEND
            type= "image/*"
            putExtra(Intent.EXTRA_STREAM, gallery.uri)
        }
        activity.startActivity(Intent.createChooser(sharingIntent, "Share Image"))
    }

    @JvmStatic
    fun sharePhoto(activity: Activity, galleries: MutableList<Gallery>) {
        val uriList = mutableListOf<Uri>()
        for (gallery in galleries) {
            gallery.uri?.let {
                uriList.add(it)
            }
        }
        val sharingIntent = Intent().apply {
            action= Intent.ACTION_SEND_MULTIPLE
            type= "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList as ArrayList<out Parcelable>)
        }
        activity.startActivity(Intent.createChooser(sharingIntent, "Share Image"))
    }

    private const val COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name"

}