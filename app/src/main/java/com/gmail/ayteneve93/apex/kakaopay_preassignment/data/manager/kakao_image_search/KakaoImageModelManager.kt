package com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search

import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModelList
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils
import com.google.gson.*
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDate
import kotlin.collections.ArrayList

/**
 * 카카오 Image Api 를 사용해서 Query 한 내용을
 * 호출자에게 반환하는 기능을 가집니다.
 * DI를 통해 관리되는 SingleTon 클래스입니다.
 *
 * @property baseUrl 기본적인 Kakao Api URL 입니다.
 * @property kakaoImageModelGson KakaoImageModelDeserializer 과 KakaoImageModelList 를 사용해 만든 Gson 입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class KakaoImageModelManager{

    private val baseUrl = "https://dapi.kakao.com"

    /**
     * 카카오 Image Api 에서 받아온 데이터를 효과적으로 Json 화 하기 위해 작성한
     * JsonDeserializer 입니다. Api에서 받아온 정보를 설계한 모델에 맞게 파싱하여 저장하고 반환합니다.
     */
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

    /**
     * 외부에서 호출자가 Api 를 실질적으로 호출하는 메소드입니다.
     *
     * @param queryKeyword 검색할 문자열입니다
     * @param sortOption 검색 정렬 기준입니다
     * @param pageNumber 검색 페이지 번호입니다
     * @param size 한 페이지에 표현하는 이미지의 갯수입니다
     * @return Rx Single, 제네릭은 KakaoImageModelList 를 리턴하며 호출자는 subscribe 하여 사용합니다.
     *
     * @see com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSearchApi
     */
    fun rxKakaoImageSearchByKeyword(queryKeyword : String, sortOption: KakaoImageSortOption, pageNumber : Int, size : Int) : Single<KakaoImageModelList>{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(kakaoImageModelGson))
            .build()
            .create(KakaoImageSearchApi::class.java)
            .getImages(queryKeyword, sortOption.optionString, pageNumber, size)
    }

}

