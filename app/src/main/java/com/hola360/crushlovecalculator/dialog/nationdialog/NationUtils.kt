package com.hola360.crushlovecalculator.dialog.nationdialog

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.caverock.androidsvg.SVGImageView
import com.google.gson.Gson
import com.hola360.crushlovecalculator.R
import java.io.InputStream

object NationUtils {

    fun generateEmptyNation(context: Context): NationModel {
        return NationModel().apply {
            nationInformation = NationInformation().apply {
                name = context.resources.getString(R.string.no_result)
            }
        }
    }

    @JvmStatic
    fun generateNationList(context: Context): MutableList<NationModel> {
        val nations = mutableListOf<NationModel>()
        val nationData = generateNationData(context)
        for (nation in nationData.nationInformation) {
            val flagResId = getNationFlagResId(context, nation)
            nations.add(NationModel().apply {
                this.nationFlagId = flagResId
                nationInformation = nation
            })
        }
        return nations
    }

    @JvmStatic
    fun generateNationData(context: Context): NationData {
        val jsonString = readJsonFile(context, "nation_data", "country_flag.json")
        return Gson().fromJson(jsonString, NationData::class.java)
    }

    fun readJsonFile(context: Context, rootPath: String, fileName: String): String {
        var inputStream: InputStream? = null
        var jsonString = ""
        try {
            inputStream = context.assets.open("$rootPath/$fileName")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            jsonString = String(buffer)
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return jsonString
    }

    private fun getNationFlagResId(context: Context, nationInfor: NationInformation): Int {
        return context.resources.getIdentifier(
            "ic_nation_flag_${
                nationInfor.code.toString().lowercase()
            }", "drawable", context.packageName
        )
    }

    @BindingAdapter("android:loadFlag")
    @JvmStatic
    fun SVGImageView.loadFlag(nationModel: NationModel?) {
        val flag = if(nationModel!!.nationInformation!!.flag == null){
            "flag/no_result.svg"
        } else {
            nationModel.nationInformation!!.flag
        }
        setImageAsset(flag)
    }

    @BindingAdapter("android:loadNationName")
    @JvmStatic
    fun TextView.loadNationName(nationName: String?) {
        this.text = nationName ?: this.resources.getString(R.string.profile_default_country)
    }

}