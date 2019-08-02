package com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search

import com.gmail.ayteneve93.apex.kakaopay_preassignment.BuildConfig
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModelList
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
interface KakaoImageSearchApi {

    @Headers("Authorization: KakaoAK ${BuildConfig.KakaoImageSearchApiKey}")
    @GET("v2/search/image")
    fun getImages(
        @Query(value = "query", encoded = true) queryKeyword : String,
        @Query(value = "sort" , encoded = true) sortOptionString : String,
        @Query(value = "page" , encoded = true) pageNumber : Int,
        @Query(value = "size" , encoded = true) size : Int
    ) : Single<KakaoImageModelList>

}