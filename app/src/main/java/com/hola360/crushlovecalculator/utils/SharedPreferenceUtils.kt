package com.hola360.crushlovecalculator.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.hola360.crushlovecalculator.data.model.ProfileModel

class SharedPreferenceUtils private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getUserProfileModel(isCrush: Boolean): ProfileModel {
        val profileString = getStringValue(
            if (isCrush) {
                "Key_crush_profile"
            } else {
                "Key_profile"
            }
        )
        return if (profileString.isNullOrEmpty()) {
            ProfileModel()
        } else {
            Gson().fromJson(profileString, ProfileModel::class.java)
        }
    }

    fun setUserProfileModel(isCrush: Boolean, profileModel: ProfileModel) {
        putStringValue(
            if (isCrush) {
                "Key_crush_profile"
            } else {
                "Key_profile"
            }, Gson().toJson(profileModel)
        )
    }


    fun setLoveBg(path: String?) {
        putStringValue("Key_LovingBg", path)
    }

    fun getLoveBg(): String? {
        return getStringValue("Key_LovingBg")
    }


    fun setStartLovingDate(time: Long) {
        putLongValue("Key_StartLovingDate", time)
    }

    fun getStartLovingDate(): Long {
        return getLongValue("Key_StartLovingDate")
    }

    fun putStringValue(key: String?, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value).apply()
    }

    fun getStringValue(key: String?): String? {
        return sharedPreferences.getString(key, "")
    }

    fun putBooleanValue(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value).apply()
    }

    fun getBooleanValue(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun putIntValue(key: String?, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value).apply()
    }

    fun getIntValue(key: String?): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun putLongValue(key: String?, value: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(key, value).apply()
    }

    fun getLongValue(key: String?): Long {
        return sharedPreferences.getLong(key, 0L)
    }

    companion object {
        const val PREFERENCE_NAME = "CrushLoveCalculator"
        private var instance: SharedPreferenceUtils? = null
        fun getInstance(context: Context): SharedPreferenceUtils {
            if (instance == null) {
                instance = SharedPreferenceUtils(context)
            }
            return instance!!
        }
    }

}