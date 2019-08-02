package com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search

import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
enum class KakaoImageSortOption(val optionString : String) {
    ACCURACY("accuracy"), RECENCY("recency");
    companion object {
        fun getSortOptionFromString(optionString : String) : KakaoImageSortOption = values().find { it.optionString == optionString }!!
    }
}