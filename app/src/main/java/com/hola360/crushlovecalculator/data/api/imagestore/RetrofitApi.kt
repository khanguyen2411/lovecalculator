package com.hola360.crushlovecalculator.data.api.imagestore

import androidx.room.util.StringUtil
import com.hola360.crushlovecalculator.data.api.ResultObject
import com.hola360.crushlovecalculator.data.model.submit.SubmitRootData
import com.hola360.crushlovecalculator.data.model.theme.ThemeStoreResponse
import com.hola360.crushlovecalculator.data.response.imagestore.ImageStoreReponse
import com.hola360.crushlovecalculator.data.response.imagestore.commonevent.CommonEventResponse
import com.hola360.crushlovecalculator.data.response.imagestore.emoijconnect.EmoijConnectResponse
import com.hola360.crushlovecalculator.data.response.imagestore.storeBg.CoverStoreResponse
import com.hola360.crushlovecalculator.data.response.imagestore.wearther.WeatherResponse
import com.hola360.crushlovecalculator.utils.Constants
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitApi {
    @FormUrlEncoded
    @POST(Constants.STORE_PATH)
    suspend fun getImagesStore(
        @Field("type") type: String,
        @Field("sort") sort: String,
        @Field("page") page: Int,
        @Field("limit") limit: Int
    ): ImageStoreReponse?

    @FormUrlEncoded
    @POST(".")
    fun getDiscoverData(
        @Field("score") score: Int,
        @Field("country") countryCode: String
    ): Call<ResultObject?>

    @FormUrlEncoded
    @POST(Constants.STORE_PATH)
    suspend fun getTheme(
        @Field("type") type: String,
        @Field("sort") sort: String,
    ): ThemeStoreResponse?

    @FormUrlEncoded
    @POST(Constants.STORE_PATH)
    suspend fun pushCount(
        @Field("type") type: String,
        @Field("item_id") itemId: String,
        @Field("update_type") pushType: String,
    ): SubmitRootData?

    @FormUrlEncoded
    @POST(Constants.STORE_PATH)
    suspend fun getIconEmoji(@Field("type") type: String): EmoijConnectResponse

    @FormUrlEncoded
    @POST(Constants.COMMON_EVENT_PATH)
    suspend fun getCommonEvent(@Field("type") type: String) : CommonEventResponse

    @FormUrlEncoded
    @POST(Constants.STORE_PATH)
    suspend fun getCoverStore(@Field("type") type: String) : CoverStoreResponse

    @FormUrlEncoded
    @POST(Constants.WEATHER_PATH)
    suspend fun getWeatherIcon(@Field("type") type: String) : WeatherResponse
}