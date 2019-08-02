package com.gmail.ayteneve93.apex.kakaopay_preassignment.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption

private const val USER_PREF_KEY = "utils.User.KEY"
@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class PreferenceUtils(
    application: Application
) {
    private val userPreference : SharedPreferences = application.getSharedPreferences(USER_PREF_KEY, Context.MODE_PRIVATE)

    fun getSortOption() : KakaoImageSortOption =
        KakaoImageSortOption.getSortOptionFromString(userPreference.getString(PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION.attributeName, KakaoImageSortOption.ACCURACY.optionString)!!)
    fun setSortOption(sortOption : KakaoImageSortOption) =
        userPreference.edit().putString(PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION.attributeName, sortOption.optionString).apply()

}

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
object PreferenceCategory {
    enum class User(val attributeName : String) {
        KAKAO_IMAGE_SORT_OPTION("utils.PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION")
    }
}