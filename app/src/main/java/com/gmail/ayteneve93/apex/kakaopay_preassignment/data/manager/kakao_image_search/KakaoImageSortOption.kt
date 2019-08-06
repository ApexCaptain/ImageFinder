package com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search

import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils

/**
 * 카카오 Image Api 에서 사용하는 검색 옵션입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
enum class KakaoImageSortOption(val optionString : String) {
    /** 정확도 순 */
    ACCURACY("accuracy"),
    /** 최신 순 */
    RECENCY("recency");
    companion object {
        /** String 을 입력하면 그에 맞는 SortOption 을 Retrun 합니다.
         *
         * @param optionString 입력한 문자열
         * @return 입력한 문자열에 해당하는 SortOption
         */
        fun getSortOptionFromString(optionString : String) : KakaoImageSortOption = values().find { it.optionString == optionString }!!
    }
}