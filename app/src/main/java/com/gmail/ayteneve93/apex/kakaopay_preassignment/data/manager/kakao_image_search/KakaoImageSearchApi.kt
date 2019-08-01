package com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search

import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModelList
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

@Suppress("spellCheckingInspection")
interface KakaoImageSearchApi {

    @Headers("Authorization: KakaoAK 096880bf3645de9453d183477e88f9f2")
    @GET("v2/search/image")
    fun getImages(
        @Query(value = "query", encoded = true) queryKeyword : String,
        @Query(value = "sort", encoded = true) sortOptionString : String,
        @Query(value = "page", encoded = true) page : Int,
        @Query(value = "size", encoded = true) size : Int
    ) : Single<KakaoImageModelList>

}