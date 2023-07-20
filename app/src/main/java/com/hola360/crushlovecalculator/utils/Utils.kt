package com.hola360.crushlovecalculator.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.DialogFragment
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.PhotoModel
import java.io.*
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


object Utils {
    private const val FILE_HEADER_TIME_FORMAT = "EE, MMM dd yyyy"
    private val HEADER_FORMAT =
        SimpleDateFormat(FILE_HEADER_TIME_FORMAT, Locale.ROOT)


    fun roundDecimalNumber(str: String): Float {
        val decimal = BigDecimal(str).setScale(1, RoundingMode.HALF_EVEN)
        return decimal.toFloat()
    }

    fun saveTempBitmap(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        val file = File(context.cacheDir, fileName)
        return if (file.exists()) {
            file.delete()
            if (!file.exists()) {
                return getTempBitmapUri(file, bitmap)
            } else {
                null
            }
        } else {
            getTempBitmapUri(file, bitmap)
        }
    }

    private fun getTempBitmapUri(file: File, bitmap: Bitmap): Uri? {
        return try {
            file.createNewFile()
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            Uri.parse(file.absolutePath)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    fun isLargeBitmap(bitmap: Bitmap): Boolean {
        return bitmap.width > MAX_BITMAP_SIZE || bitmap.height > MAX_BITMAP_SIZE
    }

    fun resizeLargeBitmap(bitmap: Bitmap): Bitmap {
        val scaleX = MAX_BITMAP_SIZE / bitmap.width.toFloat()
        val scaleY = MAX_BITMAP_SIZE / bitmap.height.toFloat()
        val scale = if (scaleX <= scaleY) {
            scaleX
        } else {
            scaleY
        }
        val maxtrix = Matrix().apply {
            postScale(scale, scale)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, maxtrix, false)
    }

    fun cropHeartBorderImage(context: Context, sourceBitmap: Bitmap): Bitmap? {
        val drawable: Drawable? =
            ContextCompat.getDrawable(context, R.drawable.ic_lovetest_photo_default)
        val defaultPhoto = drawable?.toBitmap(
            drawable.intrinsicWidth.coerceAtLeast(1),
            drawable.intrinsicHeight.coerceAtLeast(1),
            null
        )
        return if (defaultPhoto != null) {
            val scaleX = defaultPhoto.width / sourceBitmap.width.toFloat()
            val scaleY = defaultPhoto.height / sourceBitmap.height.toFloat()
            val matrix = Matrix().apply {
                postScale(scaleX, scaleY)
            }
            val newBitmap = Bitmap.createBitmap(
                sourceBitmap,
                0,
                0,
                sourceBitmap.width,
                sourceBitmap.height,
                matrix,
                false
            )
            val paint = Paint().apply {
                isAntiAlias = true
                xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            }
            val bitmap = Bitmap.createBitmap(
                defaultPhoto.width,
                defaultPhoto.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            canvas.drawBitmap(defaultPhoto, 0f, 0f, null)
            canvas.drawBitmap(newBitmap, 0f, 0f, paint)
            bitmap
        } else {
            null
        }
    }


    private const val MAX_BITMAP_SIZE = 1024

    fun timeInGroup(context: Context, nowCalendar: Calendar, fileCalendar: Calendar): String {
        return if (nowCalendar[Calendar.YEAR] == fileCalendar[Calendar.YEAR] &&
            nowCalendar[Calendar.DAY_OF_YEAR] == fileCalendar[Calendar.DAY_OF_YEAR]
        ) {
            context.resources.getString(R.string.today_last_modified)
        } else if (nowCalendar[Calendar.YEAR] == fileCalendar[Calendar.YEAR] &&
            nowCalendar[Calendar.DAY_OF_YEAR] == fileCalendar[Calendar.DAY_OF_YEAR] + 1
        ) {
            context.resources.getString(R.string.yesterday_last_modified)
        } else {
            getFileHeaderTime(fileCalendar.timeInMillis)
        }
    }

    private fun getFileHeaderTime(time: Long): String {
        return HEADER_FORMAT.format(time)
    }

    fun getStringFromFile(file: File): String? {
        return try {
            val mRandomAccessFile = RandomAccessFile(file, "r")
            val size = mRandomAccessFile.length().toInt()
            val buffer = ByteArray(size)
            mRandomAccessFile.read(buffer)
            mRandomAccessFile.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }

    fun fetchPrimaryTextColor(context: Context): Int {
        val typedValue = TypedValue()
        val a: TypedArray =
            context.obtainStyledAttributes(
                typedValue.data,
                intArrayOf(android.R.attr.textColorPrimary)
            )
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    fun isShowDialog(dialog: DialogFragment?): Boolean {
        return dialog != null
                && dialog.dialog != null
                && !dialog.isHidden
                && !dialog.isRemoving
    }

    fun isnImageFromServer(path: String): Boolean {
        return path.startsWith("https://") || path.startsWith("http://")
    }

    fun checkImage(uri: Uri, context: Context): Boolean {
        val input: InputStream?
        try {
            input = context.contentResolver.openInputStream(uri)
        } catch (ex: Exception) {
            return false
        }
        val onlyBoundsOptions = BitmapFactory.Options()
        onlyBoundsOptions.inJustDecodeBounds = true
        onlyBoundsOptions.inDither = true

        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888

        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        input?.close()
        if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
            return false
        }
        return true
    }

    fun convertPixelToDp(px: Int, context: Context): Int {
        return px / (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun listToJson(list: List<Any>): String {
        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        return gson.toJson(list)
    }

    fun jsonToList(jsonString: String): List<PhotoModel> {
        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        val type: Type = object : TypeToken<List<PhotoModel?>?>() {}.type
        return gson.fromJson(jsonString, type)
    }

    infix fun <T> Collection<T>.deepEqualTo(other: Collection<T>): Boolean {
        // check collections aren't same
        if (this !== other) {
            // fast check of sizes
            if (this.size != other.size) return false
            val areNotEqual = this.asSequence()
                .zip(other.asSequence())
                // check this and other contains same elements at position
                .map { (fromThis, fromOther) -> fromThis == fromOther }
                // searching for first negative answer
                .contains(false)
            if (areNotEqual) return false
        }
        // collections are same or they are contains same elements with same order
        return true
    }
}