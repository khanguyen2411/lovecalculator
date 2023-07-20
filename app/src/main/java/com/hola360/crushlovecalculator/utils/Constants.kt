package com.hola360.crushlovecalculator.utils

import android.Manifest
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.ui.event.popup.EventActionModel

object Constants {
    val STORAGE_PERMISSION_UNDER_STORAGE_SCOPE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val STORAGE_PERMISSION_STORAGE_SCOPE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val TAKE_PICTURE_PERMISSION = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    const val JSON_THEME_FILE = "theme.json"
    const val THEME_BIG_HEART = "big_heart.svg"
    const val THEME_EMOJI = "emoji.svg"
    const val THEME_BACKGROUND = "bg.png"
    const val THEME_AVATAR = "default_avatar.png"
    const val DEFAULT_THEME_PATH = "theme/theme.zip"

    //    const val DEFAULT_THEME_PATH = "theme/Archive.zip"
    const val ASSETS_PATH: String = "file:///android_asset/"
    const val EVENT_COVER_SUB_URL = "https://files.daily4love.com/content-phase2/cove-event/"
    const val PHOTO_PATH: String = "LoveCalculator"
    const val REGEX_FILENAME = "^$|(?!.*\\.\\.)(?!.*\\.\$)[^\\W][\\s\\w.]{0,29}\$"
    const val BASE_URL = "https://dev.daily4love.com"
    const val STORE_PATH = "/phase2.php"
    const val COMMON_EVENT_PATH = "/phase2.php"
    const val WEATHER_PATH = "/phase2.php"
    const val WEATHER_SUB ="https://files.daily4love.com/content-phase2/weather/"
    const val COMMON_EVENT_TYPE = "list_event"
    const val COMMON_EVENT_SUB_URL = "https://files.daily4love.com/content-phase2/common-event/"
    const val ROWS_PER_PAGE = 4
    const val LIST_BG_PARAM = "list_bg"
    const val LIST_WEATHER_PARAM = "list_weather"
    const val LIST_THEME_PARAM = "list_theme"
    const val PUSH_COUNT = "update_count_bg"
    const val PUSH_COUNT_THEME = "update_count_theme"
    const val AT_LEAST_COLUMN = 2
    const val EMOIJ_PARAM = "list_connect_emoji"
    const val BANNER = "list_banner"
    const val TEMP_ZIP_FILE = "temp.zip"
    const val BACKGROUND_CROP_HEIGHT = 1920
    const val BACKGROUND_CROP_WIDTH = 1080
    const val TEST_PHOTO_CROP_HEIGHT = 500
    const val TEST_PHOTO_CROP_WIDTH = 500
    const val PROFILE_PHOTO_CROP_HEIGHT = 500
    const val PROFILE_PHOTO_CROP_WIDTH = 500


    const val STORY_IMAGE_MAXIMUM = 5
    const val CLICK_DELAY = 1000L
    const val CALENDAR_MIDDLE_MONTH_POSITION = 1
    val ACTION_STORY = mutableListOf(
        EventActionModel(R.drawable.ic_action_edit, "Edit Story"),
        EventActionModel(R.drawable.ic_action_delete, "Delete Story")
    )
}