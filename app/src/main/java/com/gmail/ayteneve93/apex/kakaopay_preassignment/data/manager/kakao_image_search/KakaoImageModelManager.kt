package com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search

import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModelList
import com.google.gson.*
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDate
import kotlin.collections.ArrayList


@Suppress("spellCheckingInspection")
class KakaoImageModelManager{

    private val baseUrl = "https://dapi.kakao.com"

    private class KakaoImageModelDeserializer : JsonDeserializer<KakaoImageModelList> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): KakaoImageModelList {

            val jsonObject = json!!.asJsonObject
            val metaData = jsonObject.getAsJsonObject("meta")
            val kakaoImageModelList = KakaoImageModelList(
                metaData.get("is_end").asBoolean,
                metaData.get("pageable_count").asInt,
                metaData.get("total_count").asInt,
                ArrayList()
            )
            jsonObject.getAsJsonArray("documents").forEach {
                val eachJsonObject = it.asJsonObject
                kakaoImageModelList.documents.add(
                    KakaoImageModel(
                        eachJsonObject.get("collection").asString,
                        LocalDate.parse(eachJsonObject.get("datetime").asString.substring(0..9)),
                        eachJsonObject.get("display_sitename").asString,
                        eachJsonObject.get("doc_url").asString,
                        eachJsonObject.get("height").asInt,
                        eachJsonObject.get("image_url").asString,
                        eachJsonObject.get("thumbnail_url").asString,
                        eachJsonObject.get("width").asInt
                    )
                )
            }
            return kakaoImageModelList
        }
    }

    private val kakaoImageModelGson = GsonBuilder()
        .registerTypeAdapter(
            KakaoImageModelList::class.java,
            KakaoImageModelDeserializer()
        ).create()

    fun rxKakaoImageSearchByKeyword(keyword : String, sortOption: KakaoImageSortOption, page : Int, size : Int = 20) : Single<KakaoImageModelList>{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(kakaoImageModelGson))
            .build()
            .create(KakaoImageSearchApi::class.java)
            .getImages(keyword, sortOption.optionString, page, size)
    }

}

