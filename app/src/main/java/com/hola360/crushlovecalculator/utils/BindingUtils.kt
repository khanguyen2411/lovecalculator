package com.hola360.crushlovecalculator.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGImageView
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.imageview.ShapeableImageView
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.ImageStore
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.data.model.event.EventModel
import com.hola360.crushlovecalculator.data.model.lovetime.LoveTimeModel
import com.hola360.crushlovecalculator.dialog.action.model.ActionModel
import com.hola360.crushlovecalculator.ui.event.popup.EventActionModel
import com.hola360.crushlovecalculator.ui.lovediary.model.Date
import com.hola360.crushlovecalculator.ui.lovetest.CustomScrollView
import com.hola360.crushlovecalculator.utils.listener.SafeMenuClickListener
import java.util.concurrent.TimeUnit

@BindingAdapter("android:setStoryTime")
fun TextView.setStoryTime(time: Long) {
    text = SystemUtils.getStoryTime(time)
}

@BindingAdapter("android:setDetailStoryTime")
fun TextView.setDetailStoryTime(time: Long) {
    text = SystemUtils.getStoryDetailTime(time)
}

@BindingAdapter("android:setEventTime")
fun TextView.setEventTime(time: Long) {
    text = SystemUtils.getEventTime(time)
}

@BindingAdapter("android:setDayOfWeekstEventTime")
fun TextView.setDayOfWeekstEventTime(time: Long) {
    text = SystemUtils.getDayOfWeeksEventTime(time)
}

@BindingAdapter("android:setMonthEventTime")
fun TextView.setMonthEventTime(time: Long) {
    text = SystemUtils.getMonthEventTime(time)
}

@BindingAdapter("android:setDiaryTime")
fun TextView.setDiaryTime(time : Long){
    text = SystemUtils.getDiaryTime(time)
}

@BindingAdapter("android:setLoveTimeOwnerNameDialog")
fun TextView.setLoveTimeOwnerNameDialog(loveTimeModel: LoveTimeModel?) {
    text = if (loveTimeModel != null) {
        if (loveTimeModel.profileModel != null && !loveTimeModel.profileModel!!.name.isNullOrEmpty()) {
            loveTimeModel.profileModel!!.name!!
        } else {
            context.getString(R.string.love_time_honey_name_dialog)
        }
    } else {
        context.getString(R.string.love_time_honey_name_dialog)
    }
}

@BindingAdapter("android:setLoveTimeCrushNameDialog")
fun TextView.setLoveTimeCrushNameDialog(loveTimeModel: LoveTimeModel?) {
    text = if (loveTimeModel != null) {
        if (loveTimeModel.crushProfile != null && !loveTimeModel.crushProfile!!.name.isNullOrEmpty()) {
            loveTimeModel.crushProfile!!.name!!
        } else {
            context.getString(R.string.love_time_my_beloved_name_dialog)
        }
    } else {
        context.getString(R.string.love_time_my_beloved_name_dialog)
    }
}

@BindingAdapter("android:setOwnerAvatarDialog")
fun ImageView.setOwnerAvatarDialog(loveTimeModel: LoveTimeModel?) {
    if (loveTimeModel?.profileModel != null) {
        Glide.with(this.context).load(loveTimeModel.profileModel!!.avatarUrl)
            .placeholder(R.drawable.ic_lovetest_heart_shadow)
            .error(R.drawable.ic_lovetest_heart_shadow)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    val bitmap = resource.toBitmap(
                        resource.intrinsicWidth.coerceAtLeast(1),
                        resource.intrinsicHeight.coerceAtLeast(1), null
                    )
                    val cropBitmap =
                        Utils.cropHeartBorderImage(this@setOwnerAvatarDialog.context, bitmap)
                    this@setOwnerAvatarDialog.setImageBitmap(cropBitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    this@setOwnerAvatarDialog.setImageResource(R.drawable.ic_lovetest_heart_shadow)
                }
            })
    } else {
        setImageResource(R.drawable.ic_lovetest_heart_shadow)
    }
}

@BindingAdapter("android:setCrushAvatarDialog")
fun ImageView.setCrushAvatarDialog(loveTimeModel: LoveTimeModel?) {
    if (loveTimeModel?.crushProfile != null) {
        Glide.with(this.context).load(loveTimeModel.crushProfile!!.avatarUrl)
            .placeholder(R.drawable.ic_lovetest_heart_shadow)
            .error(R.drawable.ic_lovetest_heart_shadow)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    val bitmap = resource.toBitmap(
                        resource.intrinsicWidth.coerceAtLeast(1),
                        resource.intrinsicHeight.coerceAtLeast(1), null
                    )
                    val cropBitmap =
                        Utils.cropHeartBorderImage(this@setCrushAvatarDialog.context, bitmap)
                    this@setCrushAvatarDialog.setImageBitmap(cropBitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    this@setCrushAvatarDialog.setImageResource(R.drawable.ic_lovetest_heart_shadow)
                }
            })
    } else {
        setImageResource(R.drawable.ic_lovetest_heart_shadow)
    }
}

@BindingAdapter("android:startLoveTime")
fun TextView.startLoveTime(loveTimeModel: LoveTimeModel?) {
    if (loveTimeModel != null && loveTimeModel.startDate != 0L) {
        text = SystemUtils.getDateLoveTime(loveTimeModel.startDate)
    } else {
        setText(R.string.start_love_time_now)
    }
}


@BindingAdapter("android:countLovingTime")
fun TextView.countLovingTime(loveTimeModel: LoveTimeModel?) {
    text = if (loveTimeModel != null && loveTimeModel.startDate != 0L) {
        val nowInMilliSeconds = System.currentTimeMillis()
        val deltaTime = nowInMilliSeconds - loveTimeModel.startDate
        val days = 1 + (TimeUnit.MILLISECONDS.toDays(deltaTime))
        days.toString()
    } else {
        "0"
    }
}

@BindingAdapter("android:applyLoveTheme")
fun TextView.applyLoveTheme(loveTimeModel: LoveTimeModel?) {
    if (loveTimeModel != null) {
        setTextColor(Color.parseColor(loveTimeModel.theme!!.themeColor.trim()))
    } else {
        setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
    }
}

@BindingAdapter("android:svgResource")
fun SVGImageView.svgResource(svg: SVG?) {
    if (svg != null) {
        try {
            setSVG(svg)
        } catch (ex: Exception) {
            setImageResource(R.drawable.ic_connect_emoji)
        }
    } else {
        setImageDrawable(null)
    }
}

@BindingAdapter("android:setOwnerLoveTimeAvatar")
fun ShapeableImageView.setOwnerLoveTimeAvatar(loveTimeModel: LoveTimeModel?) {
    if (loveTimeModel?.profileModel != null) {
        if (loveTimeModel.profileModel!!.avatarUrl != null && loveTimeModel.profileModel!!.avatarUrl!!.isNotEmpty()) {
            Glide.with(this.context).load(loveTimeModel.profileModel!!.avatarUrl)
                .placeholder(R.drawable.ic_your_avatar)
                .error(R.drawable.ic_your_avatar)
                .into(this)
        } else {
//            Glide.with(this.context).load(loveTimeModel.avatarFile)
//                .placeholder(R.drawable.ic_your_avatar)
//                .error(R.drawable.ic_your_avatar)
//                .into(this)

            Glide.with(this.context).load(loveTimeModel.avatarFile).apply(
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            )
                .placeholder(R.drawable.ic_your_avatar)
                .error(R.drawable.ic_your_avatar)
                .into(this)
        }
    } else {
        setImageResource(R.drawable.ic_your_avatar)
    }
}

@BindingAdapter("android:setCrushLoveTimeAvatar")
fun ShapeableImageView.setCrushLoveTimeAvatar(loveTimeModel: LoveTimeModel?) {
    if (loveTimeModel?.crushProfile != null) {
        if (loveTimeModel.crushProfile!!.avatarUrl != null && loveTimeModel.crushProfile!!.avatarUrl!!.isNotEmpty()) {
            Glide.with(this.context).load(loveTimeModel.crushProfile!!.avatarUrl)
                .placeholder(R.drawable.ic_your_avatar)
                .error(R.drawable.ic_your_avatar)
                .into(this)
        } else {
            Glide.with(this.context).load(loveTimeModel.avatarFile).apply(
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            )
                .placeholder(R.drawable.ic_your_avatar)
                .error(R.drawable.ic_your_avatar)
                .into(this)
        }
    } else {
        setImageResource(R.drawable.ic_your_avatar)
    }

}

@BindingAdapter("android:setLoveTimeOwnerName")
fun TextView.setLoveTimeOwnerName(loveTimeModel: LoveTimeModel?) {
    if (loveTimeModel != null) {
        if (loveTimeModel.theme != null) {
            val nameColor = try {
                Color.parseColor(loveTimeModel.theme!!.nameColor.trim())
            } catch (ex: Exception) {
                ContextCompat.getColor(context, R.color.md_grey_500)
            }
            setTextColor(nameColor)
            if (loveTimeModel.theme!!.hasNameFrame == 1) {
                setBackgroundResource(R.drawable.bg_love_time_your_name)
                val color = try {
                    Color.parseColor(loveTimeModel.theme!!.frameColor.trim())
                } catch (ex: Exception) {
                    ContextCompat.getColor(context, R.color.md_grey_500)
                }
                background.setTint(color)
            } else {
                setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
            }
        }
        text =
            if (loveTimeModel.profileModel != null && !loveTimeModel.profileModel!!.name.isNullOrEmpty()) {
                loveTimeModel.profileModel!!.name!!
            } else {
                context.getString(R.string.love_time_your_name)
            }
    } else {
        text = context.getString(R.string.love_time_your_name)
    }
}

@BindingAdapter("android:setLoveTimeCrushName")
fun TextView.setLoveTimeCrushName(loveTimeModel: LoveTimeModel?) {
    if (loveTimeModel != null) {
        if (loveTimeModel.theme != null) {
            val nameColor = try {
                Color.parseColor(loveTimeModel.theme!!.nameColor.trim())
            } catch (ex: Exception) {
                ContextCompat.getColor(context, R.color.md_grey_500)
            }
            setTextColor(nameColor)
            if (loveTimeModel.theme!!.hasNameFrame == 1) {
                setBackgroundResource(R.drawable.bg_love_time_crush_name)
                val color = try {
                    Color.parseColor(loveTimeModel.theme!!.frameColor.trim())
                } catch (ex: Exception) {
                    ContextCompat.getColor(context, R.color.md_grey_500)
                }
                background.setTint(color)
            } else {
                setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
            }
        }
        text =
            if (loveTimeModel.crushProfile != null && !loveTimeModel.crushProfile!!.name.isNullOrEmpty()) {
                loveTimeModel.crushProfile!!.name!!
            } else {
                context.getString(R.string.love_time_crush_name)
            }
    } else {
        text = context.getString(R.string.love_time_crush_name)
    }
}

@BindingAdapter("android:setLovingTimeBg")
fun ImageView.setLovingTimeBg(loveTimeModel: LoveTimeModel?) {
    if (loveTimeModel?.bgPath != null) {
        if (!Utils.isnImageFromServer(loveTimeModel.bgPath!!)) {
            Glide.with(this.context).load(loveTimeModel.bgPath).apply(
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            )
                .error(R.drawable.bg_love_time_default)
                .into(this)

        } else {
            Glide.with(this.context).load(loveTimeModel.bgPath)
                .error(R.drawable.bg_love_time_default)
                .into(this)
        }

    } else {
        setImageResource(R.drawable.bg_love_time_default)
    }
}

@BindingAdapter("android:arrangeLayout")
fun ConstraintLayout.arrangeLayout(loveTimeModel: LoveTimeModel?) {
    if (loveTimeModel != null) {
        updateLayoutParams<ConstraintLayout.LayoutParams> {
            when (loveTimeModel.theme!!.themeType) {
                0 -> {
                    topToTop = R.id.guideLineDivide
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                }
                1 -> {
                    topToTop = ConstraintLayout.LayoutParams.UNSET
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

                }
                else -> {
                    topToTop = R.id.guideLineDivide
                    bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                }
            }

        }
    } else {
        updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToTop = R.id.guideLineDivide
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }
}

@BindingAdapter("android:topHeartLayout")
fun ConstraintLayout.topHeartLayout(loveTimeModel: LoveTimeModel?) {
    if (loveTimeModel != null) {
        updateLayoutParams<ConstraintLayout.LayoutParams> {
            when (loveTimeModel.theme!!.themeType) {
                0 -> {
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    bottomToTop = R.id.guideLineDivide
                    matchConstraintPercentWidth = 0.75f
                }
                else -> {
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    bottomToTop = ConstraintLayout.LayoutParams.UNSET
                    matchConstraintPercentWidth = if (loveTimeModel.theme!!.hasBigHeart == 1) {
                        0.75f
                    } else {
                        0.5f
                    }
                }
            }

        }
    } else {
        updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToTop = R.id.guideLineDivide
            matchConstraintPercentWidth = 0.75f
        }
    }
}


@BindingAdapter("android:iconForAction")
fun ImageView.iconForAction(actionModel: ActionModel) {
    if (actionModel.selectMode) {
        visibility = View.VISIBLE
        if (actionModel.selected) {
            setImageResource(R.drawable.ic_my_photo_radio_check)
        } else {
            setImageResource(R.drawable.ic_my_photo_radio_button_unchecked)
        }
        setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
    } else {
        if (actionModel.icon == -1) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
            setImageResource(actionModel.icon)
        }
        setColorFilter(Utils.fetchPrimaryTextColor(context))
    }

}

@BindingAdapter("android:bindThumbnailFile")
fun ShapeableImageView.bindThumbnailFile(photoModel: PhotoModel?) {
    if (photoModel != null) {
        Glide.with(this).load(photoModel.file)
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image).into(this)
    } else {
        Glide.with(this).load(R.drawable.ic_default_image)
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image).into(this)
    }
}

@BindingAdapter("android:bindImageStore")
fun ShapeableImageView.bindImageStore(thumbPath: String?) {
    if (thumbPath != null) {
        Glide.with(this).load(thumbPath)
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image).into(this)
    } else {
        Glide.with(this).load(R.drawable.ic_default_image)
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image).into(this)
    }
}

@BindingAdapter("android:bindPickingPhoto")
fun ShapeableImageView.bindPickingPhoto(imageModel: PhotoModel?) {
    if (imageModel != null && !imageModel.isNormalItem) {
        setImageResource(R.drawable.bg_story_add_image)
        scaleType = ImageView.ScaleType.FIT_XY
    } else {
        scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(this).load(imageModel?.uriString)
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image).into(this)
    }
}

@BindingAdapter("android:bindFullImageStore")
fun ImageView.bindFullImageStore(imageStore: ImageStore?) {
    if (imageStore?.full != null) {
        Glide.with(this).load(imageStore.full)
            .placeholder(R.drawable.ic_bg_preview_image_store_2)
            .error(R.drawable.ic_bg_preview_image_store_2).into(this)
    } else {
        Glide.with(this).load(R.drawable.ic_bg_preview_image_store_2)
            .placeholder(R.drawable.ic_bg_preview_image_store_2)
            .error(R.drawable.ic_bg_preview_image_store_2).into(this)
    }
}

@BindingAdapter("android:bindEventThumb")
fun ShapeableImageView.bindEventThumb(thumbPath: String) {
    if (thumbPath.isNotEmpty()) {
        Glide.with(this).load(thumbPath)
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image).into(this)
    } else {
        setImageResource(R.drawable.banner_default)
    }
}

@BindingAdapter("android:bindStoryDetail")
fun PhotoView.bindStoryDetail(uri: String) {
    Glide.with(this).load(uri).into(this)
}

@BindingAdapter("android:countAction")
fun TextView.countAction(count: Int) {
    text = SystemUtils.convertCount(count.toLong())
}

@BindingAdapter("android:textViewDrawable")
fun TextView.textViewDrawable(isFavorite: Boolean) {
    val drawable = if (isFavorite) {
        R.drawable.ic_detail_favorite_selected
    } else {
        R.drawable.ic_detail_favorite_18
    }

    setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
}

@BindingAdapter("android:noResultSrc")
fun ImageView.noResultSrc(isCommon: Boolean) {
    if (isCommon) {
        setImageResource(R.drawable.ic_img_no_internet)
    } else {
        setImageResource(R.drawable.ic_empty_my_event)
    }
}

@BindingAdapter("android:setEventDate")
fun TextView.setEventDate(date: Long) {
    text = SystemUtils.getEventDate(date)
}

@BindingAdapter("android:setDayLeft")
fun TextView.setDayLeft(date: Long) {
    val dayLeft = SystemUtils.calDayLeft(date).toString() + " days left"
    text = dayLeft
}

@BindingAdapter("android:iconForAction")
fun ImageView.iconForAction(eventActionModel: EventActionModel) {
    setImageResource(eventActionModel.icon)
}

@SuppressLint("CheckResult")
@BindingAdapter("android:bindEvent")
fun ImageView.bindEvent(link: String) {
    if (link.isNotEmpty()) {
        Glide.with(this)
            .load(Constants.COMMON_EVENT_SUB_URL + link)
            .into(this)
    } else {
        setImageResource(R.drawable.ic_default_image)
    }
}

@SuppressLint("CheckResult")
@BindingAdapter("android:bindEventBg")
fun ImageView.bindEventBg(link: String) {
    if (link.isNotEmpty()) {
        Glide.with(this)
            .load(Constants.COMMON_EVENT_SUB_URL + link)
            .placeholder(R.drawable.ic_loading_event2)
            .error(R.drawable.ic_loading_event2)
            .into(this)
    } else {
        setImageResource(R.drawable.ic_default_image)
    }
}

@BindingAdapter("android:bindEventCover")
fun ImageView.bindEventCover(thumbPath: String) {
    if (thumbPath.isNotEmpty()) {
        Glide.with(this)
            .load(thumbPath)
            .placeholder(R.drawable.ic_loading_event2)
            .error(R.drawable.ic_loading_event2)
            .into(this)
    } else {
        setImageResource(R.drawable.banner_default)
    }
}

@BindingAdapter("android:scrollable")
fun CustomScrollView.scrollable(isEmpty: Boolean) {
    setScrollable(isEmpty)
}

fun View.clickWithDebounce(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun View.clickWithDebounce2(debounceTime: Long = 800L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun Toolbar.setSafeMenuClickListener(onSafeClick: (MenuItem?) -> Unit) {
    val safeMenuClickListener = SafeMenuClickListener(defaultInterval = 500, onSafeClick = ({
        onSafeClick(it)
    }))
    setOnMenuItemClickListener(safeMenuClickListener)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("android:bindMaximum")
fun TextView.bindMaximum(max: Int) {
    text =
        resources.getString(R.string.pick_photo_story_message) + " $max " + resources.getString(R.string.pick_photo_story_photo)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("android:countPicked")
fun TextView.countPicked(count: Int) {
    text = "$count"
}

@BindingAdapter("android:setMyEventDate")
fun TextView.setMyEventDate(eventModel: EventModel) {
    text = SystemUtils.getMyEventTime(eventModel)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("android:setMyEventCount")
fun TextView.setMyEventCount(eventModel: EventModel) {
    when (eventModel.calType) {
        1 -> {
            text = if (eventModel.date < System.currentTimeMillis()) {
                resources.getString(R.string.action_done)
            } else {
                val daysLeft = SystemUtils.calMyEventDayLeft(eventModel.date) + 1
                if (daysLeft / 365L == 0L) {
                    "$daysLeft Days Left"
                } else {
                    if (daysLeft % 365 == 0L) {
                        "${daysLeft / 365} Years Left"
                    } else {
                        "${daysLeft / 365} Years ${(daysLeft % 365)} Days Left"
                    }
                }
            }
        }
        2 -> {
            val daysAgo = SystemUtils.calMyEventDayLeft(eventModel.date)
            text = if (daysAgo / 365L == 0L) {
                "$daysAgo Days Ago"
            } else {
                if (daysAgo % 365 == 0L) {
                    "${daysAgo / 365} Years Ago"
                } else {
                    "${daysAgo / 365} Years ${(daysAgo % 365)} Days Ago"
                }
            }
        }
        3 -> {
            val cal = SystemUtils.calAnnuallyType(eventModel.date)
            text = if (cal == -1L) {
                "Today"
            } else {
                "${cal + 1} Days Left"
            }
        }
    }
}

@BindingAdapter("android:setupDateBackground")
fun ConstraintLayout.setupBackground(date: Date) {
    setBackgroundResource(
        if (date.isSelected) {
            R.drawable.bg_date_selected
        } else {
            if (date.isNowDate) {
                R.drawable.bg_date_current_date
            } else {
                R.drawable.bg_date_normal
            }
        }
    )
}

@BindingAdapter("android:setupDateText")
fun TextView.setupDateText(date: Date) {
//    setTextColor(
//        if (date.isNowDate) {
//            Color.parseColor("#8295DB")
//        } else {
//            if (date.isSelected) {
//                Color.WHITE
//            } else {
//                if(date.isSelfMonthDate){
//                    Color.parseColor("#222B45")
//                } else {
//                    Color.parseColor("#8F9BB3")
//                }
//            }
//        }
//    )

    setTextColor(
        if (date.isSelected) {
            Color.WHITE
        } else {
            if (date.isNowDate) {
                Color.parseColor("#8295DB")
            } else {
                if(date.isSelfMonthDate){
                    Color.parseColor("#222B45")
                } else {
                    Color.parseColor("#8F9BB3")
                }
            }
        }
    )

    text = "${date.date}"
}


@BindingAdapter("android:setupDateView")
fun View.setupDateView(date: Date) {
    visibility = if (date.hasDiary) {
        setBackgroundResource(if (date.isSelected) R.drawable.bg_date_dot_selected else R.drawable.bg_date_dot_normal)
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

@BindingAdapter("android:setTextTime")
fun TextView.setTextTime(time: Long){
    text = SystemUtils.getTimeOnLy(time)
}

