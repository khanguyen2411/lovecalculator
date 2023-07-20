@file:Suppress("DEPRECATION")

package com.hola360.crushlovecalculator.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.os.StatFs
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.snackbar.Snackbar
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.app.App
import com.hola360.crushlovecalculator.data.LovePhotoModel
import com.hola360.crushlovecalculator.data.model.event.EventModel
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow

object SystemUtils {
    private const val FULL_DATE_TIME = "MMMM dd, yyyy, HH:mm"
    private const val BIRTHDAY_TIME = "MMMM dd, yyyy"
    private const val PHOTO_TITLE_TIME = "EE, MMM dd yyyy"
    private const val MSG_TIME_ONLY = "HH:mm"
    private const val LOVE_DATE_TIME = "MMM dd, yyyy"
    private const val LOVE_DATE_TIME_FORMAT = "%s %02d, %s"
    private const val EVENT_DATE = "MMM dd, yyyy (EEE)"
    private const val EVENT_TIME = "MMM dd, yyyy (EE)"
    private const val DAY_OF_WEEKS_EVENT_TIME = "(EE)"
    private const val DIARY_TIME = "EE,MMM dd,yyyy HH:mm"
    private const val MONTH_EVENT_TIME = "MMM"
    private const val STORY_TIME = "MMM dd, yyyy"
    private const val CURRENT_DAY_STORY_TIME = "MMM dd"
    private const val MILLIS_PER_DAY = 86400000L
    private const val DIARY_FORMAT_DATE = "EEE, MMM dd, yyyy"
    private const val MONTH_YEAR_FORMAT = "MMM, yyyy"

    fun getMonthYearFormat(date: Long) : String{
        val sdf = SimpleDateFormat(MONTH_YEAR_FORMAT, Locale.US)
        return sdf.format(date)
    }

    fun getDiaryDate(date: Long) : String{
        val sdf = SimpleDateFormat(DIARY_FORMAT_DATE, Locale.US)
        return sdf.format(date)
    }

    fun getDayOfWeeksEventTime(date: Long): String {
        val sdfDate = SimpleDateFormat(DAY_OF_WEEKS_EVENT_TIME, Locale.US)
        return sdfDate.format(date)
    }

    fun getDiaryTime(date :Long) :String{
        val sdfDate = SimpleDateFormat(DIARY_TIME,Locale.US)
        return sdfDate.format(date)
    }

    fun getMonthEventTime(date: Long): String {
        val sdfDate = SimpleDateFormat(MONTH_EVENT_TIME, Locale.US)
        return sdfDate.format(date)
    }

    fun getStoryTime(date: Long): String {
        val sdfDate = SimpleDateFormat(STORY_TIME, Locale.US)
        val storyTime = sdfDate.format(date)
        val currentDay = sdfDate.format(System.currentTimeMillis())

        return if (storyTime == currentDay) {
            val format = SimpleDateFormat(CURRENT_DAY_STORY_TIME, Locale.US)
            format.format(date)
        } else {
            storyTime
        }
    }

    fun getStoryDetailTime(date: Long): String {
        val sdfDate = SimpleDateFormat(STORY_TIME, Locale.US)
        return sdfDate.format(date)
    }

    fun calDayLeft(date: Long): Long {
        val currentTime = System.currentTimeMillis()
        val eventDate = if (date < currentTime) {
            date + MILLIS_PER_DAY * 365
        } else {
            date
        }

        return abs(((eventDate - currentTime) / MILLIS_PER_DAY))
    }

    private const val SECOND_IN_MILLIS = 1000L
    private const val MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60
    private const val HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60
    private const val DAY_IN_MILLIS = HOUR_IN_MILLIS * 24
    const val WEEK_IN_MILLIS = DAY_IN_MILLIS * 7

    fun calMyEventDayLeft(date: Long): Long {
        val currentTime = System.currentTimeMillis()
        return abs(((date - currentTime) / MILLIS_PER_DAY))
    }

    fun getEventTime(date: Long): String {
        val sdfDate = SimpleDateFormat(EVENT_TIME, Locale.US)
        return sdfDate.format(date)
    }

    fun getMyEventTime(eventModel: EventModel): String {
        val sdfDate = SimpleDateFormat(EVENT_TIME, Locale.US)
        val currentDate = Date(System.currentTimeMillis())
        var eventDate = Date(eventModel.date)

        if (sdfDate.format(currentDate) == sdfDate.format(eventDate)) {
            return sdfDate.format(currentDate)
        } else {
            if (eventModel.calType == 3) {
                eventDate = calAnnually(currentDate, eventDate)
            }
        }
        return sdfDate.format(eventDate.time)
    }

    fun calAnnuallyType(date: Long): Long {
        val currentTime = System.currentTimeMillis()
        val currentDate = Date(currentTime)
        var eventDate = Date(date)

        if (SimpleDateFormat("dd, MM", Locale.US).format(currentDate) ==
            SimpleDateFormat("dd, MM", Locale.US).format(eventDate)
        ) {
            return -1
        } else {
            eventDate = calAnnually(currentDate, eventDate)
        }

        return abs(((eventDate.time - currentTime) / MILLIS_PER_DAY))
    }

    fun calAnnually(currentDate: Date, eventDate: Date) : Date{
        if (eventDate.year < currentDate.year) {
            if (eventDate.month < currentDate.month) {
                eventDate.year = currentDate.year + 1
            } else {
                if (eventDate.month == currentDate.month) {
                    if (eventDate.date < currentDate.date) {
                        eventDate.year = currentDate.year + 1
                    } else {
                        if(eventDate.date == currentDate.date){
                            eventDate.year = currentDate.year
                        }
                    }
                }
            }
        } else {
            if (eventDate.year > currentDate.year) {
                eventDate.year = currentDate.year + 1
            } else {
                if (eventDate.month < currentDate.month) {
                    eventDate.year = currentDate.year + 1
                } else {
                    if (eventDate.month == currentDate.month) {
                        if (eventDate.date <= currentDate.date) {
                            eventDate.year = currentDate.year + 1
                        }
                    }
                }
            }
        }
        return eventDate
    }

    fun getEventDate(date: Long): String {
        val currentTime = System.currentTimeMillis()
        val eventDate = if (date < currentTime) {
            date + MILLIS_PER_DAY * 365
        } else {
            date
        }
        val sdf = SimpleDateFormat(EVENT_DATE, Locale.US)
        return sdf.format(eventDate)
    }

    fun getDateLoveTime(date: Long): String {
        val sdfDate = SimpleDateFormat(LOVE_DATE_TIME, Locale.US)
        return sdfDate.format(date)
    }

    fun getDateLoveTime(context: Context, year: Int, month: Int, day: Int): String {
        val months = context.resources.getStringArray(R.array.months_short_hand)
        return String.format(Locale.US, LOVE_DATE_TIME_FORMAT, months[month], day, year)
    }

    fun getDateLoveTimeInMillisecond(fullLoveDate: String): Date? {
        val sdfDate = SimpleDateFormat(LOVE_DATE_TIME, Locale.US)
        return sdfDate.parse(fullLoveDate)
    }

    @JvmStatic
    fun getFullDateTime(date: Long): String {
        val sdfDate = SimpleDateFormat(FULL_DATE_TIME, Locale.US)
        return sdfDate.format(date)
    }

    @JvmStatic
    fun getBirthdayTime(date: Long): String {
        val sdfDate = SimpleDateFormat(BIRTHDAY_TIME, Locale.US)
        return sdfDate.format(date)
    }

    @JvmStatic
    fun getPhotoTitleTime(date: Long): String {
        val sdfDate = SimpleDateFormat(PHOTO_TITLE_TIME, Locale.US)
        return sdfDate.format(date * 1000)
    }

    @JvmStatic
    fun getTimeOnLy(date: Long): String {
        val sdfDate = SimpleDateFormat(MSG_TIME_ONLY, Locale.US)
        return sdfDate.format(date)
    }

    @JvmStatic
    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (permission in permissions) {
                val notGiant = ActivityCompat.checkSelfPermission(
                    context,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
                if (notGiant) {
                    return false
                }
            }
        }
        return true
    }

    @JvmStatic
    fun hasShowRequestPermissionRationale(
        context: Context?,
        vararg permissions: String?
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (permission in permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (context as Activity?)!!,
                        permission!!
                    )
                ) {
                    return true
                }
            }
        }
        return false
    }

    @JvmStatic
    fun allPermissionGrant(intArray: IntArray): Boolean {
        var isGranted = true
        for (permission in intArray) {
            if (permission != PackageManager.PERMISSION_GRANTED) {
                isGranted = false
                break
            }
        }
        return isGranted
    }

    @JvmStatic
    fun getBackgroundColor(context: Context): Int {
        val a = TypedValue()
        context.theme.resolveAttribute(android.R.attr.windowBackground, a, true)
        return a.data
    }

    @JvmStatic
    fun isTablet(resources: Resources): Boolean {
        return resources.configuration.smallestScreenWidthDp >= 600
    }

    @JvmStatic
    fun isLandscape(resources: Resources): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    @JvmStatic
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    @JvmStatic
    fun isEqualAndroidQ(): Boolean {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
    }

    @JvmStatic
    fun isAndroidN(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    @JvmStatic
    fun isAndroidM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    @JvmStatic
    fun isAndroidO(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    @JvmStatic
    fun isAndroidQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    @JvmStatic
    fun isAndroidR(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    @JvmStatic
    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    @JvmStatic
    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    @JvmStatic
    fun convertPixelsToDp(px: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    @JvmStatic
    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    @JvmStatic
    fun getStoragePermissions(): Array<String> {
        return if (isAndroidR()) {
            Constants.STORAGE_PERMISSION_STORAGE_SCOPE
        } else {
            Constants.STORAGE_PERMISSION_UNDER_STORAGE_SCOPE
        }
    }

    fun recordAudioPermissionGrant(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun showAlertPermissionNotGrant(binding: ViewDataBinding, activity: Activity) {
        if (!hasShowRequestPermissionRationale(activity, *getStoragePermissions())) {
            val snackBar = Snackbar.make(
                binding.root,
                activity.resources.getString(R.string.goto_settings),
                Snackbar.LENGTH_LONG
            )
            snackBar.setAction(
                activity.resources.getString(R.string.settings)
            ) { view: View? ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }
            snackBar.show()
        } else {
            Toast.makeText(
                activity,
                activity.resources.getString(R.string.grant_permission),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @JvmStatic
    fun screenShot(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun Bitmap.toRoundedCorners(
        topLeftRadius: Float = 0F,
        topRightRadius: Float = 0F,
        bottomRightRadius: Float = 0F,
        bottomLeftRadius: Float = 0F
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(
            width, // width in pixels
            height, // height in pixels
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        // the bounds of a round-rectangle to add to the path
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())

        // float array of 8 values, 4 pairs of [x,y] radii
        val radii = floatArrayOf(
            topLeftRadius, topLeftRadius, // top left corner
            topRightRadius, topRightRadius, // top right corner
            bottomRightRadius, bottomRightRadius, // bottom right corner
            bottomLeftRadius, bottomLeftRadius // bottom left corner
        )

        // path to draw rounded corners bitmap
        val path = Path().apply {
            // add a closed round-rectangle contour to the path
            // each corner receives two radius values [x, y]
            addRoundRect(
                rectF,
                radii,
                // the direction to wind the round-rectangle's contour
                Path.Direction.CCW
            )
        }

        // intersect the current clip with the specified path
        canvas.clipPath(path)

        // draw the rounded corners bitmap on canvas
        canvas.drawBitmap(this, 0f, 0f, null)
        return bitmap
    }


    @JvmStatic
    fun getCaptureMatrix(data: ByteArray, frontCamera: Boolean): Matrix {
        val inputStream: InputStream = ByteArrayInputStream(data)
        var ei: ExifInterface? = null
        try {
            ei = ExifInterface(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val orientation = ei!!.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val rotateDegree: Int = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
        val matrix = Matrix()
        matrix.postRotate((rotateDegree).toFloat())
        if (frontCamera) {
            matrix.postScale(-1f, 1f)
        }
        return matrix
    }

    fun shareMedia(context: Context, uris: MutableList<Uri>) {
        val sharingIntent = if (uris.size == 1) {
            Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uris[0])
            }
        } else {
            Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                type = "image/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris as ArrayList<out Parcelable>)
            }
        }
        context.startActivity(Intent.createChooser(sharingIntent, "Share Image"))
    }

    fun shareUrl(context: Context, url: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

//    fun sendEmailRateApp(context: Context, rateNumber:Int, feedBack: String?){
//        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("contact@hola360.com"))
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.rate_app_email_subject))
//        var content= String.format("%s\n%s", context.resources.getString(R.string.rate_app_email_content_1),
//            String.format("%s %s", context.resources.getString(R.string.rate_app_email_content_2), rateNumber))
//        if(!feedBack.isNullOrEmpty()){
//            content+= "\n"
//            content+= "- ".plus(feedBack)
//        }
//        emailIntent.putExtra(Intent.EXTRA_TEXT, content)
//        context.startActivity(emailIntent)
//    }

    fun getInternalStorageInformation(file: File): String {
        val iStat = StatFs(file.path)
        val iBlockSize = iStat.blockSizeLong
        val availableBlock = iStat.availableBlocksLong
        val totalBlock = iStat.blockCountLong
        val availableSpace = formatSize(availableBlock * iBlockSize)
        val totalSpace = formatSize(totalBlock * iBlockSize)
        return String.format("$availableSpace free of $totalSpace")
    }

    fun getInternalStoragePercent(file: File): Float {
        val iStat = StatFs(file.path)
        val availableBlock = iStat.availableBlocksLong
        val totalBlock = iStat.blockCountLong
        return availableBlock.toFloat() / totalBlock
    }

    fun formatSize(storageSize: Long): String? {
        var size = storageSize
        var suffix: String? = null
        if (size >= STORAGE_FACTOR) {
            suffix = "KB"
            size /= STORAGE_FACTOR
            if (size >= STORAGE_FACTOR) {
                suffix = "MB"
                size /= STORAGE_FACTOR
                if (size >= STORAGE_FACTOR) {
                    suffix = "GB"
                    size /= STORAGE_FACTOR
                }
            }
        }
        val resultBuffer = StringBuilder(size.toString())
        var commaOffset = resultBuffer.length - 3
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',')
            commaOffset -= 3
        }
        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }


    fun checkConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            when (connectivityManager.activeNetworkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> true
                ConnectivityManager.TYPE_MOBILE -> true
                ConnectivityManager.TYPE_ETHERNET -> true
                else -> false
            }
        }
    }

    @Throws(SecurityException::class)
    fun deleteFiles(
        context: Context,
        localPhotoModels: List<LovePhotoModel>
    ): MutableList<LovePhotoModel> {
        val remainingItems = mutableListOf<LovePhotoModel>()
        for (localSongModel in localPhotoModels) {
            try {
                val deletedId = context.contentResolver.delete(
                    localSongModel.uri!!,
                    null,
                    null
                ).toLong()
                if (deletedId > 0) {
                    localSongModel.file!!.delete()
                    context.contentResolver.notifyChange(Uri.parse("content://media"), null)
                } else {
                    if (isAndroidQ()) {
                        remainingItems.add(localSongModel)
                    }
                }
            } catch (e: NullPointerException) {
            }
        }
        return remainingItems
    }

    @JvmStatic
    fun hideSoftKeyboard(activity: Activity, view: View) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @JvmStatic
    fun showSoftKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    const val STORAGE_FACTOR = 1000

    interface OnStorageRequestResult {
        fun onGranted()
        fun onDeny()
    }

    fun getColumn(app: App): Int {
        val dp =
            (app.resources.getDimension(R.dimen.gallery_item_width) / app.resources.displayMetrics.density).toInt()
        return (app.screenWidthInDp / dp).coerceAtLeast(Constants.AT_LEAST_COLUMN)
    }

    fun getStoryImageColumn(app: App): Int {
        val dp =
            (app.resources.getDimension(R.dimen.dp_120) / app.resources.displayMetrics.density).toInt()
        return (app.screenWidthInDp / dp).coerceAtLeast(Constants.AT_LEAST_COLUMN)
    }

    fun convertCount(count: Long): String {
        val unit = 1000
        if (count < unit) return count.toString()
        val exp = (ln(count.toDouble()) / ln(unit.toDouble())).toInt()
        val pre = "KMB"[exp - 1].toString() + ""
        return String.format(
            "%d%s",
            (count / unit.toDouble().pow(exp.toDouble())).toInt(),
            pre
        )
    }
}