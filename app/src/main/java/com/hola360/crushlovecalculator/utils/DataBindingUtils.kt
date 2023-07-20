package com.hola360.crushlovecalculator.utils

import android.graphics.*
import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.imageview.ShapeableImageView
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.ProfileModel
import com.hola360.crushlovecalculator.data.model.lovetime.LoveTimeModel
import java.util.*

object DataBindingUtils {

    @JvmStatic
    @BindingAdapter("setProfileAvatar")
    fun ImageView.setProfileAvatar(url:String?){
        Glide.with(this.context).load(url)
            .placeholder(R.drawable.ic_profile_default_avatar).error(R.drawable.ic_profile_default_avatar)
            .into(this)
    }

    @JvmStatic
    @BindingAdapter("setHomeAvatar")
    fun ShapeableImageView.setHomeAvatar(url:String?){
        Glide.with(this.context).load(url)
            .placeholder(R.drawable.ic_person).error(R.drawable.ic_person)
            .into(this)
    }

    @JvmStatic
    @BindingAdapter("setLovetestPhoto")
    fun ImageView.setLoveTestPhoto(url:String?){
        Glide.with(this.context).load(url)
            .placeholder(R.drawable.ic_lovetest_photo_default).error(R.drawable.ic_lovetest_photo_default)
            .into(object : CustomTarget<Drawable>(){
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?) {
                    val bitmap= resource.toBitmap(resource.intrinsicWidth.coerceAtLeast(1),
                        resource.intrinsicHeight.coerceAtLeast(1), null)
                    val cropBitmap = Utils.cropHeartBorderImage(this@setLoveTestPhoto.context, bitmap)
                    this@setLoveTestPhoto.setImageBitmap(cropBitmap)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    this@setLoveTestPhoto.setImageResource(R.drawable.ic_lovetest_photo_default)
                }
            })
    }

    @JvmStatic
    @BindingAdapter("setProfileHeight")
    fun EditText.setProfileHeight(height:Float?){
        height?.let {
            if(height>0f){
                this.setText(height.toString())
            }
        }
    }

    @JvmStatic
    @BindingAdapter("setHeightUnit")
    fun TextView.setHeightUnit(isCm:Boolean?){
        isCm?.let {
            this.text= if(it){
                this.context.getString(R.string.profile_height_unit_cm)
            }else{
                this.context.getString(R.string.profile_height_unit_inch)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("setWeightUnit")
    fun TextView.setWeightUnit(isKg:Boolean?){
        isKg?.let {
            this.text= if(it){
                this.context.getString(R.string.profile_weight_unit_kg)
            }else{
                this.context.getString(R.string.profile_weight_unit_pound)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("setBirthdayTime")
    fun TextView.setBirthdayTime(time:Long?){
        this.text=if(time==null){
            val calendar= Calendar.getInstance()
            val curYear= calendar.get(Calendar.YEAR)
            calendar.set(Calendar.YEAR, curYear-16)
            SystemUtils.getBirthdayTime(calendar.timeInMillis)
        }else{
            SystemUtils.getBirthdayTime(time)
        }
    }

    @JvmStatic
    @BindingAdapter("setTestBirthdayTime")
    fun TextView.setTestBirthdayTime(time:Long?){
        time?.let {
            this.text= SystemUtils.getBirthdayTime(time)
        }
    }

    @JvmStatic
    @BindingAdapter("setHeartPercent")
    fun ImageView.setHeartPercent(heartType:Int?, heartPercent:Int?){
        if(heartPercent != null && heartType!= null){
            val percent= (heartPercent- heartType* PERCENT_PER_HEART).coerceAtMost(PERCENT_PER_HEART)
            val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_test_result_dialog_heart)
            val defaultPhoto= drawable?.toBitmap( drawable.intrinsicWidth.coerceAtLeast(1),
                drawable.intrinsicHeight.coerceAtLeast(1), null)
            defaultPhoto?.let {
                val canvas= Canvas(it)
                canvas.drawBitmap(defaultPhoto, 0f, 0f, null)
                val percentBitmap= Bitmap.createBitmap(defaultPhoto.width, defaultPhoto.height, Bitmap.Config.ARGB_8888)
                val percentCanvas= Canvas(percentBitmap)
                percentCanvas.drawBitmap(percentBitmap, 0f, 0f, null)
                val percentPaintWidth= (1- HEART_PADDING_PERCENT)*defaultPhoto.width*percent/ PERCENT_PER_HEART
                val percentPath= Path()
                val percentPathX= (HEART_PADDING_PERCENT*defaultPhoto.width + percentPaintWidth.toFloat())/2
                percentPath.moveTo(percentPathX.toFloat(), 0f)
                percentPath.lineTo(percentPathX.toFloat(), defaultPhoto.height.toFloat())
                val percentPaint= Paint().apply {
                    this.isAntiAlias= true
                    this.style=Paint.Style.STROKE
                    this.strokeWidth= percentPaintWidth.toFloat()
                }
                percentCanvas.drawPath(percentPath, percentPaint)
                val xrefPaint= Paint().apply {
                    this.isAntiAlias=true
                    this.xfermode= PorterDuffXfermode(PorterDuff.Mode.DST_IN)
                }
                canvas.drawBitmap(percentBitmap, 0f, 0f, xrefPaint)
                this.setImageBitmap(it)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("resultBmiWeight")
    fun TextView.resultBmiWeight(profile: ProfileModel?){
        profile?.let {
            val weightPrefix= this.context.getString(R.string.love_test_result_weight)
            val weight= profile.weight.toString()
            val weightUnit= LoveTestUtils.getWeightUnit(this.context, profile)
            this.text= String.format("%s %s %s", weightPrefix, weight, weightUnit)
        }
    }

    @JvmStatic
    @BindingAdapter("resultBmiHeight")
    fun TextView.resultBmiHeight(profile: ProfileModel?){
        profile?.let {
            val heightPrefix= this.context.getString(R.string.love_test_result_height)
            val height= profile.height.toString()
            val heightUnit= LoveTestUtils.getHeightUnit(this.context, profile)
            this.text= String.format("%s %s %s", heightPrefix, height, heightUnit)
        }
    }


    private const val PERCENT_PER_HEART=20
    private const val HEART_PADDING_PERCENT= 0.1445

}