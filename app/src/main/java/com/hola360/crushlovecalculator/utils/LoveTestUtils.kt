package com.hola360.crushlovecalculator.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.ProfileModel
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.*
import kotlin.math.abs

object LoveTestUtils {

    //test Photo
    fun getPhotoMatch(context: Context, yourPhotoUrl:String, crushPhotoUrl:String):Int{
        val yourValue= generateValueFromBitmap(context, yourPhotoUrl)
        val crushValue= generateValueFromBitmap(context, crushPhotoUrl)
        return calculateMatchResult(yourValue, crushValue)
    }

    private fun generateValueFromBitmap(context: Context, bitmapUrl: String): Int{
        val bitmap= MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(bitmapUrl))
        val byteArrayOutputStream= ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream)
        val bitmapBytes= byteArrayOutputStream.toByteArray()
        return generateMD5ValueFromByteArray(bitmapBytes)
    }

    //test Name
    fun getNameMatch(yourName:String, crushName:String):Int{
        val yourValue= generateValueFromName(yourName)
        val crushValue= generateValueFromName(crushName)
        return calculateMatchResult(yourValue, crushValue)
    }

    private fun generateValueFromName(name:String):Int{
        val nameByteArray= name.toByteArray()
        return generateMD5ValueFromByteArray(nameByteArray)
    }

    //test Birthday
    fun getBirthdayMatch(yourBirthday:Long, crushBirthday:Long):Int{
        val yourTime= yourBirthday/1000000
        val yourValue= yourTime.toInt()% MAX_LOVE_MATCH
        val crushTime= crushBirthday/1000000
        val crushValue= crushTime.toInt()% MAX_LOVE_MATCH
        return calculateMatchResult(yourValue, crushValue)
    }

    //test Nation

    //test BMI
    fun getBMIMatch(yourHeight:Float, yourHeightInCm:Boolean, yourWeight:Float, yourWeightInKg:Boolean,
        crushHeight:Float, crushHeightInCm:Boolean, crushWeight:Float, crushWeightInKg: Boolean):Int{
        val yourBMI= generateBMIValue(yourHeight, yourHeightInCm, yourWeight, yourWeightInKg)
        val crushBMI= generateBMIValue(crushHeight, crushHeightInCm, crushWeight, crushWeightInKg)
        return calculateMatchResult((yourBMI*10).toInt(), (crushBMI*10).toInt())
    }

    private fun generateBMIValue(height:Float, heightUnitCm:Boolean, weight:Float, weightUnitKg:Boolean):Float{
        val heightInMeter= if(!heightUnitCm){
            height* INCH_TO_CM_SCALE/100
        }else{
            height/100
        }
        val weightInKg= if(!weightUnitKg){
            weight* POUND_TO_KG_SCALE
        }else{
            weight
        }
        val rawBMI= weightInKg/(heightInMeter*heightInMeter)
        return Utils.roundDecimalNumber(rawBMI.toString())
    }

    //test Fingerprint
    fun getFingerprintMatch(yourTouchTime:Long, crushTouchTime:Long):Int{
        return calculateMatchResult(yourTouchTime.toInt(), crushTouchTime.toInt())
    }

    private fun generateMD5ValueFromByteArray(byteArray: ByteArray):Int{
        val md5Byte= MessageDigest.getInstance("MD5").digest(byteArray)
        val md5String= if(SystemUtils.isAndroidO()){
            Base64.getEncoder().encodeToString(md5Byte)
        }else{
            null
        }
        var sum=0
        return  if(md5String != null){
            md5String.forEach {
                sum += it.code
            }
            sum
        }else{
            1
        }
    }

    private fun calculateMatchResult(yourValue:Int, crushValue:Int):Int{
        val diffValue= abs(yourValue - crushValue)
        val value=   diffValue% MAX_LOVE_MATCH
        return if(value>=MIN_MATCH_VALUE){
            value
        }else{
            value+ MIN_MATCH_VALUE
        }
    }

    fun getHeightUnit(context: Context, profile: ProfileModel):String{
        return if(profile.heightUnitCm){
            context.getString(R.string.profile_height_unit_cm)
        }else{
            context.getString(R.string.profile_height_unit_inch)
        }
    }

    fun getWeightUnit(context: Context, profile: ProfileModel):String{
        return if(profile.weightUnitKg){
            context.getString(R.string.profile_weight_unit_kg)
        }else{
            context.getString(R.string.profile_weight_unit_pound)
        }
    }

    const val MAX_LOVE_MATCH=100
    private const val MIN_MATCH_VALUE=50
    private const val INCH_TO_CM_SCALE= 2.54f
    private const val POUND_TO_KG_SCALE= 0.453592f
}