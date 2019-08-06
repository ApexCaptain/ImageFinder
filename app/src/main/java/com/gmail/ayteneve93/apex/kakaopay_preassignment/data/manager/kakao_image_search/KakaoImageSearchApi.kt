package com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search

import com.gmail.ayteneve93.apex.kakaopay_preassignment.BuildConfig
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModelList
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * 카카오 Image Api 를 구현한 Interface 입니다.
 * Retrofit2 로 구성되었습니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
interface KakaoImageSearchApi {

    /**
     * 카카오 Image Api 입니다.
     *
     * @param queryKeyword 검색할 문자열입니다.
     * @param sortOptionString 검색 정렬 기준입니다.
     * @param pageNumber 검색 페이지 번호입니다.
     * @param size 한 페이지에 표시될 이미지의 갯수입니다.
     * @return Rx Single 을 Return 하며 내부 Generic Type은 KakaoImageModelList입니다.
     *
     * @see com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModelList
     */
    @Headers("Authorization: KakaoAK ${BuildConfig.KakaoImageSearchApiKey}")
    @GET("v2/search/image")
    fun getImages(
        @Query(value = "query", encoded = true) queryKeyword : String,
        @Query(value = "sort" , encoded = true) sortOptionString : String,
        @Query(value = "page" , encoded = true) pageNumber : Int,
        @Query(value = "size" , encoded = true) size : Int
    ) : Single<KakaoImageModelList>

}